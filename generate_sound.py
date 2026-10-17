import math
import struct
import wave
import os

def generate_bus_horn(filename):
    sample_rate = 44100
    duration = 0.82
    total_samples = int(sample_rate * duration)
    
    # Modern / European camper cheerful dual-tone horn (F4 349.23 Hz + A4 440.0 Hz)
    f_low = 349.23
    f_high = 440.0
    
    harmonics = [
        (1.0, 0.52),
        (2.0, 0.24),
        (3.0, 0.12),
        (4.0, 0.08),
        (5.0, 0.04),
    ]
    
    raw_samples = [0.0] * total_samples
    
    for i in range(total_samples):
        t = i / sample_rate
        env = 0.0
        # Beep 1 (0.02s - 0.16s)
        if 0.02 <= t < 0.16:
            rel_t = t - 0.02
            if rel_t < 0.015:
                env = rel_t / 0.015
            elif rel_t < 0.12:
                env = 1.0
            else:
                env = (0.14 - rel_t) / 0.02
        # Gap (0.16s - 0.22s)
        elif 0.16 <= t < 0.22:
            env = 0.0
        # Beep 2 (0.22s - 0.44s)
        elif 0.22 <= t < 0.44:
            rel_t = t - 0.22
            if rel_t < 0.018:
                env = rel_t / 0.018
            elif rel_t < 0.17:
                env = 1.0
            else:
                env = (0.22 - rel_t) / 0.05
        
        if env > 0.0:
            pitch_bend = 1.0 + 0.02 * math.exp(-((t % 0.20) * 35))
            flutter = 1.0 + 0.005 * math.sin(2 * math.pi * 16.0 * t)
            
            sig = 0.0
            for mult, weight in harmonics:
                freq1 = f_high * mult * pitch_bend * flutter
                freq2 = f_low * mult * pitch_bend * flutter
                sig += weight * (math.sin(2 * math.pi * freq1 * t) + math.sin(2 * math.pi * freq2 * t))
            
            sig = (sig / 2.0) * env
            raw_samples[i] = sig

    # Echo & Room warmth
    delay1 = int(sample_rate * 0.055)
    delay2 = int(sample_rate * 0.095)
    output = [0.0] * total_samples
    for i in range(total_samples):
        val = raw_samples[i]
        if i >= delay1:
            val += raw_samples[i - delay1] * 0.22
        if i >= delay2:
            val += raw_samples[i - delay2] * 0.12
        output[i] = val
        
    write_wav(filename, output, sample_rate)

def generate_vintage_bus_horn(filename):
    sample_rate = 44100
    duration = 0.95
    total_samples = int(sample_rate * duration)
    
    # Vintage adventure bus pneumatic / acoustic road horn (D4 293.66 Hz + F#4 369.99 Hz + A4 440 Hz major chord)
    notes = [293.66, 369.99, 440.0]
    weights = [0.45, 0.35, 0.20]
    
    raw_samples = [0.0] * total_samples
    
    for i in range(total_samples):
        t = i / sample_rate
        env = 0.0
        # Blast 1: 0.02 - 0.22s
        if 0.02 <= t < 0.22:
            rel_t = t - 0.02
            if rel_t < 0.025:
                env = rel_t / 0.025
            elif rel_t < 0.16:
                env = 1.0
            else:
                env = (0.20 - rel_t) / 0.04
        # Blast 2: 0.30 - 0.60s (Long hearty tour bus honk)
        elif 0.30 <= t < 0.60:
            rel_t = t - 0.30
            if rel_t < 0.025:
                env = rel_t / 0.025
            elif rel_t < 0.24:
                env = 1.0
            else:
                env = (0.30 - rel_t) / 0.06
                
        if env > 0.0:
            # Rich brass acoustic sound
            sig = 0.0
            for note, n_wt in zip(notes, weights):
                # 3 harmonics per note
                sig += n_wt * 0.6 * math.sin(2 * math.pi * note * t)
                sig += n_wt * 0.28 * math.sin(2 * math.pi * note * 2 * t)
                sig += n_wt * 0.12 * math.sin(2 * math.pi * note * 3 * t)
            
            raw_samples[i] = sig * env

    # Warm mountain highway echo
    delay1 = int(sample_rate * 0.070)
    delay2 = int(sample_rate * 0.140)
    output = [0.0] * total_samples
    for i in range(total_samples):
        val = raw_samples[i]
        if i >= delay1:
            val += raw_samples[i - delay1] * 0.25
        if i >= delay2:
            val += raw_samples[i - delay2] * 0.14
        output[i] = val
        
    write_wav(filename, output, sample_rate)

def write_wav(filename, output, sample_rate):
    total_samples = len(output)
    max_val = max(abs(s) for s in output) or 1.0
    scale = 0.88 / max_val
    os.makedirs(os.path.dirname(os.path.abspath(filename)), exist_ok=True)
    with wave.open(filename, 'w') as wf:
        wf.setnchannels(2)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        frames = bytearray()
        for i in range(total_samples):
            s_left = output[i]
            s_right = output[i - int(sample_rate * 0.0015)] if i >= int(sample_rate * 0.0015) else output[i]
            sample_int_l = int(max(-32767, min(32767, s_left * scale * 32767)))
            sample_int_r = int(max(-32767, min(32767, s_right * scale * 32767)))
            frames.extend(struct.pack('<hh', sample_int_l, sample_int_r))
        wf.writeframes(frames)
    print(f"Generated {filename}")

if __name__ == '__main__':
    base_dir = r'c:\Users\Asus\OneDrive\Desktop\travel-bucket-list\travel-bucket-list\src\main\resources\static\sounds'
    generate_bus_horn(os.path.join(base_dir, 'bus-horn.wav'))
    generate_vintage_bus_horn(os.path.join(base_dir, 'bus-horn-vintage.wav'))
