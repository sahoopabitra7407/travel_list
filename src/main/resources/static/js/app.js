/**
 * WANDERLIST - Core Application State & Frontend Logic
 */

document.addEventListener('DOMContentLoaded', () => {
    // App States
    let destinations = [];
    let goals = [];
    let memories = [];
    let selectedDestinationExpenses = [];
    let selectedDestId = null;

    let currentStatusFilter = 'ALL';
    let currentCategoryFilter = 'ALL';
    let currentMoodFilter = 'ALL';
    let searchQuery = '';

    // Leaflet Map Object
    let map = null;
    let mapMarkers = [];

    // DOM Elements
    const gridEl = document.getElementById('destinations-grid');
    const emptyStateEl = document.getElementById('empty-state');
    const toastContainer = document.getElementById('toast-container');
    const themeToggleBtn = document.getElementById('theme-toggle');

    // Destinations Modals
    const destModal = document.getElementById('destination-modal');
    const destForm = document.getElementById('destination-form');
    const modalTitleEl = document.getElementById('modal-title');
    const addDestBtn = document.getElementById('add-destination-btn');
    const emptyAddBtn = document.getElementById('empty-add-btn');
    
    // Details Modal
    const detailsModal = document.getElementById('details-modal');

    // Surprise Me Modal
    const surpriseModal = document.getElementById('surprise-modal');
    const surpriseBtn = document.getElementById('surprise-me-btn');
    const wheelLoader = document.getElementById('wheel-loader');
    const wheelText = document.getElementById('wheel-text');
    const surpriseResult = document.getElementById('surprise-result');

    // Goals & Memories Modals
    const goalModal = document.getElementById('goal-modal');
    const goalForm = document.getElementById('goal-form');
    const addGoalBtn = document.getElementById('add-goal-btn');

    const memoryModal = document.getElementById('memory-modal');
    const memoryForm = document.getElementById('memory-form');
    const addMemoryBtn = document.getElementById('add-memory-btn');
    const memoryDestSelect = document.getElementById('memory-dest-select');

    const expenseModal = document.getElementById('expense-modal');
    const expenseForm = document.getElementById('expense-form');
    const addExpenseBtn = document.getElementById('add-expense-btn');

    // Search and Filters inputs
    const searchInput = document.getElementById('search-input');
    const clearSearchBtn = document.getElementById('clear-search');
    const categorySelect = document.getElementById('category-filter');
    const tabButtons = document.querySelectorAll('.tab-btn');

    // Init Theme
    initTheme();

    // Fetch and Load Initial Data
    refreshAllData();

    // Setup global listeners
    setupEventListeners();

    /* ==========================================================================
       DATA FETCHING & REFRESH
       ========================================================================== */
    async function refreshAllData() {
        showSkeletonGrid();
        await Promise.all([
            fetchDestinations(),
            fetchGoals(),
            fetchMemories()
        ]);
        updateStatistics();
        renderGrid();
        renderTimeline();
        renderGoals();
        renderMemories();
        updateMapMarkers();
        checkAchievements();
    }

    async function fetchDestinations() {
        try {
            const res = await fetch('/api/destinations');
            if (res.ok) {
                destinations = await res.json();
            }
        } catch (err) {
            console.error('Error fetching destinations:', err);
            showToast('Failed to load destinations database', 'danger');
        }
    }

    async function fetchGoals() {
        try {
            const res = await fetch('/api/goals');
            if (res.ok) {
                goals = await res.json();
            }
        } catch (err) {
            console.error('Error fetching goals:', err);
        }
    }

    async function fetchMemories() {
        try {
            const res = await fetch('/api/memories');
            if (res.ok) {
                memories = await res.json();
            }
        } catch (err) {
            console.error('Error fetching memories:', err);
        }
    }

    async function fetchExpenses(destId) {
        try {
            const res = await fetch(`/api/expenses/destination/${destId}`);
            if (res.ok) {
                selectedDestinationExpenses = await res.json();
                renderExpenses();
            }
        } catch (err) {
            console.error('Error fetching expenses:', err);
        }
    }

    /* ==========================================================================
       CORE LAYOUT RENDERING
       ========================================================================== */
    function renderGrid() {
        const filtered = destinations.filter(dest => {
            if (currentStatusFilter !== 'ALL' && dest.status !== currentStatusFilter) return false;
            if (currentCategoryFilter !== 'ALL' && dest.category !== currentCategoryFilter) return false;
            if (currentMoodFilter !== 'ALL' && dest.mood !== currentMoodFilter) return false;
            
            if (searchQuery) {
                const query = searchQuery.toLowerCase();
                const titleMatch = dest.title?.toLowerCase().includes(query);
                const countryMatch = dest.country?.toLowerCase().includes(query);
                const cityMatch = dest.city?.toLowerCase().includes(query);
                const categoryMatch = dest.category?.toLowerCase().includes(query);
                const moodMatch = dest.mood?.toLowerCase().includes(query);
                if (!titleMatch && !countryMatch && !cityMatch && !categoryMatch && !moodMatch) return false;
            }
            return true;
        });

        if (filtered.length === 0) {
            gridEl.innerHTML = '';
            emptyStateEl.classList.remove('hidden');
            return;
        }

        emptyStateEl.classList.add('hidden');
        gridEl.innerHTML = filtered.map(dest => createCardHTML(dest)).join('');
        attachCardListeners();
    }

    function createCardHTML(dest) {
        const defaultImages = {
            Culture: 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=800&q=80',
            Adventure: 'https://images.unsplash.com/photo-1526392060635-9d6019884377?auto=format&fit=crop&w=800&q=80',
            Nature: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80',
            Beach: 'https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=800&q=80',
            Mountain: 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80',
            Romantic: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=800&q=80'
        };

        const imgUrl = dest.imageUrl || defaultImages[dest.category] || 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80';
        const ratingStars = '★'.repeat(dest.rating || 5) + '☆'.repeat(5 - (dest.rating || 5));
        const statusBadgeClass = `badge-${dest.status ? dest.status.toLowerCase() : 'wishlist'}`;

        return `
            <article class="destination-card glass" data-id="${dest.id}">
                <div class="card-media">
                    <img src="${imgUrl}" alt="${escapeHTML(dest.title)}" class="card-img" onerror="this.src='https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80'">
                    <div class="card-gradient-overlay"></div>
                    <span class="status-badge ${statusBadgeClass}">${dest.status}</span>
                    <span class="category-tag">${escapeHTML(dest.category || 'General')}</span>
                </div>
                <div class="card-body">
                    <div class="card-location">
                        <i class="fa-solid fa-location-dot"></i>
                        ${escapeHTML(dest.city ? `${dest.city}, ${dest.country}` : dest.country)}
                    </div>
                    <h3 class="card-title">${escapeHTML(dest.title)}</h3>
                    <p class="card-desc">${escapeHTML(dest.description || 'No description added yet.')}</p>
                    
                    <div class="card-meta">
                        <div class="meta-item">
                            <i class="fa-solid fa-calendar-day"></i>
                            <span>${escapeHTML(dest.bestSeason || 'Year-round')}</span>
                        </div>
                        ${dest.budget ? `<div class="meta-budget">$${Number(dest.budget).toLocaleString()}</div>` : ''}
                        <div class="rating-stars">${ratingStars}</div>
                    </div>

                    <div class="card-actions">
                        <span class="journey-link" data-id="${dest.id}">View Journey <i class="fa-solid fa-arrow-right"></i></span>
                        <button class="wishlist-toggle-btn ${dest.status === 'WISHLIST' ? 'active' : ''}" data-id="${dest.id}" title="Toggle Wishlist Status">
                            <i class="fa-solid fa-heart"></i>
                        </button>
                    </div>
                </div>
            </article>
        `;
    }

    function showSkeletonGrid() {
        gridEl.innerHTML = Array(3).fill(0).map(() => `
            <div class="destination-card glass skeleton-card" style="height: 380px;">
                <div style="background: rgba(255,255,255,0.05); height: 180px;"></div>
                <div style="padding: 20px; display: flex; flex-direction: column; gap: 12px;">
                    <div style="background: rgba(255,255,255,0.05); height: 14px; width: 40%;"></div>
                    <div style="background: rgba(255,255,255,0.05); height: 22px; width: 80%;"></div>
                    <div style="background: rgba(255,255,255,0.05); height: 40px; width: 100%;"></div>
                </div>
            </div>
        `).join('');
    }

    function updateStatistics() {
        const total = destinations.length;
        const wishlist = destinations.filter(d => d.status === 'WISHLIST').length;
        const planned = destinations.filter(d => d.status === 'PLANNED').length;
        const visited = destinations.filter(d => d.status === 'VISITED').length;

        // Distinct countries
        const countries = new Set(destinations.map(d => d.country?.trim().toLowerCase()).filter(Boolean));

        animateCounter('stat-countries', countries.size);
        animateCounter('stat-total', total);
        animateCounter('stat-wishlist', wishlist);
        animateCounter('stat-visited', visited);
    }

    function animateCounter(id, targetVal) {
        const el = document.getElementById(id);
        if (!el) return;
        let curr = 0;
        const speed = Math.max(1, Math.floor(targetVal / 30));
        const timer = setInterval(() => {
            curr += speed;
            if (curr >= targetVal) {
                el.textContent = targetVal;
                clearInterval(timer);
            } else {
                el.textContent = curr;
            }
        }, 20);
    }

    /* ==========================================================================
       MAP MANAGEMENT (LEAFLET)
       ========================================================================== */
    function initMap() {
        if (map) return;
        // Centralize map viewport
        map = L.map('world-map', {
            center: [20, 0],
            zoom: 2,
            minZoom: 1.5,
            maxZoom: 10
        });

        // Dark-mode themed tiles using open CartoDB tile service
        L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
            attribution: '&copy; OpenStreetMap contributors &copy; CARTO'
        }).addTo(map);
    }

    function updateMapMarkers() {
        initMap();

        // Clear existing markers
        mapMarkers.forEach(m => map.removeLayer(m));
        mapMarkers = [];

        destinations.forEach(dest => {
            if (dest.latitude === null || dest.longitude === null) return;

            // Pin marker coloring code
            let color = '#fbbf24'; // Wishlist (Amber)
            if (dest.status === 'PLANNED') color = '#38bdf8'; // Planned (Blue)
            if (dest.status === 'VISITED') color = '#10b981'; // Visited (Emerald)

            const customIcon = L.divIcon({
                className: 'custom-map-pin',
                html: `<div style="background-color: ${color}; width: 14px; height: 14px; border-radius: 50%; border: 2px solid #fff; box-shadow: 0 0 8px ${color}"></div>`,
                iconSize: [14, 14]
            });

            const marker = L.marker([dest.latitude, dest.longitude], { icon: customIcon }).addTo(map);
            
            const popupContent = `
                <div class="map-popup-card">
                    <h4>${escapeHTML(dest.title)}</h4>
                    <p><i class="fa-solid fa-map-pin"></i> ${escapeHTML(dest.city ? `${dest.city}, ${dest.country}` : dest.country)}</p>
                    <span style="color: ${color}">${dest.status}</span>
                    <div style="font-size: 11px; margin-top: 4px; font-weight: 700; color: #4ade80;">Budget: $${dest.budget || 0}</div>
                </div>
            `;
            marker.bindPopup(popupContent);
            mapMarkers.push(marker);
        });
    }

    /* ==========================================================================
       TIMELINE RENDERING
       ========================================================================== */
    function renderTimeline() {
        const timelineContainer = document.getElementById('journey-timeline');
        if (!timelineContainer) return;

        // Filter out items without proper dates or status and sort
        const timelineItems = destinations
            .filter(d => d.status === 'VISITED' || d.status === 'PLANNED')
            .sort((a, b) => {
                const yearA = parseYear(a.targetDate) || 9999;
                const yearB = parseYear(b.targetDate) || 9999;
                return yearA - yearB;
            });

        if (timelineItems.length === 0) {
            timelineContainer.innerHTML = `<p style="text-align: center; color: var(--text-muted); font-size: 14px;">No upcoming plans or visited destinations to show in timeline.</p>`;
            return;
        }

        timelineContainer.innerHTML = timelineItems.map((dest, index) => {
            const year = parseYear(dest.targetDate) || 'Future';
            const statusClass = dest.status?.toLowerCase();
            return `
                <div class="timeline-milestone">
                    <div class="timeline-badge" style="background-color: ${dest.status === 'VISITED' ? '#10b981' : '#38bdf8'}"></div>
                    <div class="timeline-card glass hover-lift">
                        <span class="timeline-year">${year}</span>
                        <h4 class="timeline-dest">${escapeHTML(dest.title)}</h4>
                        <p style="font-size: 12px; color: var(--text-muted); margin-bottom: 4px;">
                            ${escapeHTML(dest.city ? `${dest.city}, ${dest.country}` : dest.country)}
                        </p>
                        <span class="timeline-status ${statusClass}">${dest.status}</span>
                    </div>
                </div>
            `;
        }).join('');
    }

    function parseYear(dateStr) {
        if (!dateStr) return null;
        const match = dateStr.match(/\d{4}/);
        return match ? parseInt(match[0]) : null;
    }

    /* ==========================================================================
       GOALS & ACHIEVEMENTS MANAGEMENT
       ========================================================================== */
    function renderGoals() {
        const goalsContainer = document.getElementById('goals-list');
        if (!goalsContainer) return;

        if (goals.length === 0) {
            goalsContainer.innerHTML = `<p style="color: var(--text-muted); font-size: 13px;">No travel goals defined yet.</p>`;
            return;
        }

        goalsContainer.innerHTML = goals.map(goal => {
            const percent = Math.min(100, Math.floor((goal.completed / goal.target) * 100));
            return `
                <div class="goal-item" data-id="${goal.id}">
                    <div class="goal-meta">
                        <div>
                            <strong>${escapeHTML(goal.title)}</strong>
                            <span style="font-size: 10px; padding: 2px 6px; background: rgba(255,255,255,0.06); border-radius: 4px; margin-left: 6px;">${escapeHTML(goal.category || 'General')}</span>
                        </div>
                        <div class="goal-actions">
                            <span class="goal-progress-text">${goal.completed}/${goal.target} (${percent}%)</span>
                            <button class="goal-action-btn goal-plus-btn" data-id="${goal.id}" title="Increment completed count"><i class="fa-solid fa-plus"></i></button>
                            <button class="goal-action-btn goal-delete-btn" data-id="${goal.id}" title="Delete goal"><i class="fa-solid fa-trash"></i></button>
                        </div>
                    </div>
                    <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 8px;">${escapeHTML(goal.description || '')}</p>
                    <div class="goal-bar-wrapper">
                        <div class="goal-bar-fill" style="width: ${percent}%;"></div>
                    </div>
                </div>
            `;
        }).join('');

        // Attach listeners for goals action
        document.querySelectorAll('.goal-plus-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = btn.dataset.id;
                const goal = goals.find(g => g.id == id);
                if (goal) {
                    goal.completed = Math.min(goal.target, goal.completed + 1);
                    await updateGoal(goal);
                }
            });
        });

        document.querySelectorAll('.goal-delete-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = btn.dataset.id;
                if (confirm('Delete this travel goal?')) {
                    await deleteGoal(id);
                }
            });
        });
    }

    async function updateGoal(goal) {
        try {
            const res = await fetch(`/api/goals/${goal.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(goal)
            });
            if (res.ok) {
                const updated = await res.json();
                const idx = goals.findIndex(g => g.id == goal.id);
                if (idx !== -1) goals[idx] = updated;
                renderGoals();
                checkAchievements();
            }
        } catch (err) {
            console.error(err);
        }
    }

    async function deleteGoal(id) {
        try {
            const res = await fetch(`/api/goals/${id}`, { method: 'DELETE' });
            if (res.ok) {
                goals = goals.filter(g => g.id != id);
                renderGoals();
                showToast('Goal deleted successfully', 'info');
            }
        } catch (err) {
            console.error(err);
        }
    }

    function checkAchievements() {
        const totalDestinations = destinations.length;
        const totalCountries = new Set(destinations.map(d => d.country?.trim().toLowerCase()).filter(Boolean)).size;
        const mountainVisits = destinations.filter(d => d.status === 'VISITED' && d.category === 'Mountain').length;
        const beachVisits = destinations.filter(d => d.status === 'VISITED' && d.category === 'Beach').length;
        const totalMemories = memories.length;
        const totalExplored = destinations.filter(d => d.status === 'VISITED').length;

        // Achievements triggers object mapping
        toggleAchievement('ach-first', totalDestinations >= 1);
        toggleAchievement('ach-globe', totalCountries >= 5);
        toggleAchievement('ach-mountain', mountainVisits >= 3);
        toggleAchievement('ach-beach', beachVisits >= 5);
        toggleAchievement('ach-memory', totalMemories >= 5);
        toggleAchievement('ach-crusher', totalExplored >= 10);
    }

    function toggleAchievement(id, unlocked) {
        const el = document.getElementById(id);
        if (!el) return;
        if (unlocked) {
            if (el.classList.contains('locked')) {
                el.classList.remove('locked');
                el.classList.add('unlocked');
                // Trigger quick visual check
                showToast(`Achievement Unlocked: ${el.querySelector('h4').textContent}!`, 'success');
            }
        } else {
            el.classList.add('locked');
            el.classList.remove('unlocked');
        }
    }

    /* ==========================================================================
       TRAVEL MEMORY WALL
       ========================================================================== */
    function renderMemories() {
        const memoriesContainer = document.getElementById('memories-masonry');
        if (!memoriesContainer) return;

        if (memories.length === 0) {
            memoriesContainer.innerHTML = `<p style="text-align: center; color: var(--text-muted); width: 100%; font-size: 14px;">No travel memories pinned yet.</p>`;
            return;
        }

        memoriesContainer.innerHTML = memories.map(mem => {
            return `
                <div class="memory-card glass animate-pop">
                    <img src="${mem.imageUrl}" alt="Memory" class="memory-img" onerror="this.src='https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80'">
                    <div class="memory-overlay">
                        <h4>${escapeHTML(mem.caption)}</h4>
                        <p>
                            <span>${escapeHTML(mem.memoryDate || 'On the road')}</span>
                            <button class="memory-delete-btn" data-id="${mem.id}" title="Remove photo memory">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </p>
                    </div>
                </div>
            `;
        }).join('');

        // Attach memory listeners
        document.querySelectorAll('.memory-delete-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                e.stopPropagation();
                const id = btn.dataset.id;
                if (confirm('Delete this memory photo from the board?')) {
                    await deleteMemory(id);
                }
            });
        });
    }

    async function deleteMemory(id) {
        try {
            const res = await fetch(`/api/memories/${id}`, { method: 'DELETE' });
            if (res.ok) {
                memories = memories.filter(m => m.id != id);
                renderMemories();
                checkAchievements();
                showToast('Memory photo removed', 'info');
            }
        } catch (err) {
            console.error(err);
        }
    }

    /* ==========================================================================
       TRIP BUDGET EXPENSES BREAKDOWN
       ========================================================================== */
    function renderExpenses() {
        const expenseContainer = document.getElementById('expense-list-container');
        const estTotalEl = document.getElementById('expense-est-total');
        const actTotalEl = document.getElementById('expense-act-total');

        if (selectedDestinationExpenses.length === 0) {
            expenseContainer.innerHTML = `<p style="font-size: 12px; color: var(--text-dim); text-align: center; margin: 20px 0;">No expenses logged for this trip yet.</p>`;
            estTotalEl.textContent = '$0';
            actTotalEl.textContent = '$0';
            return;
        }

        let estSum = 0;
        let actSum = 0;

        expenseContainer.innerHTML = selectedDestinationExpenses.map(exp => {
            estSum += exp.estimatedAmount || 0;
            actSum += exp.actualAmount || 0;

            const isUnder = exp.actualAmount <= exp.estimatedAmount;
            const diffClass = isUnder ? 'under' : 'over';

            return `
                <div class="expense-item">
                    <span class="expense-cat">${exp.category}</span>
                    <div class="expense-vals">
                        <span class="val-est" title="Estimated">$${exp.estimatedAmount}</span>
                        <span class="val-act ${diffClass}" title="ActualSpent">$${exp.actualAmount}</span>
                        <button class="expense-delete-btn" data-id="${exp.id}"><i class="fa-solid fa-xmark"></i></button>
                    </div>
                </div>
            `;
        }).join('');

        estTotalEl.textContent = `$${estSum.toLocaleString()}`;
        actTotalEl.textContent = `$${actSum.toLocaleString()}`;

        // Attach listeners to delete expense
        document.querySelectorAll('.expense-delete-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                const id = btn.dataset.id;
                await deleteExpense(id);
            });
        });
    }

    async function deleteExpense(id) {
        try {
            const res = await fetch(`/api/expenses/${id}`, { method: 'DELETE' });
            if (res.ok) {
                selectedDestinationExpenses = selectedDestinationExpenses.filter(e => e.id != id);
                renderExpenses();
                // Refresh top budget
                fetchDestinations().then(() => updateStatistics());
            }
        } catch (err) {
            console.error(err);
        }
    }

    /* ==========================================================================
       SURPRISE ME ENGINE
       ========================================================================== */
    function handleSurpriseMe() {
        if (destinations.length === 0) {
            showToast('No destinations available to select from.', 'info');
            return;
        }

        // Open modal and show spinner
        wheelLoader.classList.remove('hidden');
        wheelText.textContent = "Spinning the globe...";
        surpriseResult.classList.add('hidden');
        surpriseModal.classList.remove('hidden');

        setTimeout(() => {
            // Pick random destination
            const randIdx = Math.floor(Math.random() * destinations.length);
            const chosen = destinations[randIdx];

            wheelLoader.classList.add('hidden');
            wheelText.textContent = "Your Destination is Found!";
            
            // Set surprise modal contents
            const defaultScenery = 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80';
            document.getElementById('surprise-img').src = chosen.imageUrl || defaultScenery;
            document.getElementById('surprise-country').textContent = chosen.country;
            document.getElementById('surprise-title').textContent = chosen.title;
            document.getElementById('surprise-description').textContent = chosen.description || 'No description added yet.';
            document.getElementById('surprise-season').textContent = chosen.bestSeason || 'Year-round';
            document.getElementById('surprise-budget').textContent = chosen.budget ? `$${chosen.budget.toLocaleString()}` : 'TBD';

            // Save details links
            document.getElementById('surprise-plan-btn').onclick = async () => {
                chosen.status = 'PLANNED';
                await updateDestinationStatusDirectly(chosen.id, 'PLANNED');
                surpriseModal.classList.add('hidden');
            };

            document.getElementById('surprise-view-btn').onclick = () => {
                surpriseModal.classList.add('hidden');
                openDetailsModal(chosen);
            };

            surpriseResult.classList.remove('hidden');
        }, 1200);
    }

    async function updateDestinationStatusDirectly(id, status) {
        try {
            const res = await fetch(`/api/destinations/${id}/status`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status })
            });
            if (res.ok) {
                const updated = await res.json();
                const idx = destinations.findIndex(d => d.id == id);
                if (idx !== -1) destinations[idx] = updated;
                refreshAllData();
                showToast(`Destination added to planned list!`, 'success');
            }
        } catch (err) {
            console.error(err);
        }
    }

    /* ==========================================================================
       MODALS MANAGEMENT
       ========================================================================== */
    function openDestFormModal(dest = null) {
        destForm.reset();
        if (dest) {
            modalTitleEl.innerHTML = `<i class="fa-solid fa-pen-to-square"></i> Edit Destination`;
            document.getElementById('form-id').value = dest.id;
            document.getElementById('form-title').value = dest.title || '';
            document.getElementById('form-country').value = dest.country || '';
            document.getElementById('form-city').value = dest.city || '';
            document.getElementById('form-category').value = dest.category || 'Culture';
            document.getElementById('form-mood').value = dest.mood || 'Photography';
            document.getElementById('form-status').value = dest.status || 'WISHLIST';
            document.getElementById('form-target-date').value = dest.targetDate || '';
            document.getElementById('form-budget').value = dest.budget || '';
            document.getElementById('form-rating').value = dest.rating || 5;
            document.getElementById('form-image-url').value = dest.imageUrl || '';
            document.getElementById('form-season').value = dest.bestSeason || '';
            document.getElementById('form-duration').value = dest.duration || '';
            document.getElementById('form-latitude').value = dest.latitude || '';
            document.getElementById('form-longitude').value = dest.longitude || '';
        } else {
            modalTitleEl.innerHTML = `<i class="fa-solid fa-map-pin"></i> Add New Destination`;
            document.getElementById('form-id').value = '';
        }
        destModal.classList.remove('hidden');
        document.getElementById('form-title').focus();
    }

    function openDetailsModal(dest) {
        selectedDestId = dest.id;

        const defaultHero = 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=1920&q=80';
        document.getElementById('details-hero').style.backgroundImage = `linear-gradient(to bottom, rgba(15, 23, 42, 0.4) 0%, rgba(15, 23, 42, 0.95) 100%), url('${dest.imageUrl || defaultHero}')`;
        document.getElementById('details-title').textContent = dest.title;
        document.getElementById('details-country-tag').textContent = dest.country;
        document.getElementById('details-city-tag').textContent = dest.city ? `<i class="fa-solid fa-location-dot"></i> ${dest.city}` : '';
        document.getElementById('details-category-mood').textContent = `${dest.category || 'General'} / ${dest.mood || 'Scenic'}`;
        document.getElementById('details-season').textContent = dest.bestSeason || 'Year-round';
        document.getElementById('details-duration').textContent = dest.duration || 'Flexible';
        document.getElementById('details-budget').textContent = dest.budget ? `$${dest.budget.toLocaleString()}` : 'Flexible';
        document.getElementById('details-desc-text').textContent = dest.description || 'No description added yet.';

        // Status action toggle button
        const statusBtn = document.getElementById('details-status-toggle');
        if (dest.status === 'WISHLIST') {
            statusBtn.innerHTML = `<i class="fa-solid fa-plane"></i> Move to Planned`;
            statusBtn.onclick = () => changeStatusAndReload(dest.id, 'PLANNED');
        } else if (dest.status === 'PLANNED') {
            statusBtn.innerHTML = `<i class="fa-solid fa-circle-check"></i> Mark as Visited`;
            statusBtn.onclick = () => changeStatusAndReload(dest.id, 'VISITED');
        } else {
            statusBtn.innerHTML = `<i class="fa-solid fa-clock-rotate-left"></i> Revisit Wishlist`;
            statusBtn.onclick = () => changeStatusAndReload(dest.id, 'WISHLIST');
        }

        // Details actions
        document.getElementById('details-edit-btn').onclick = () => {
            detailsModal.classList.add('hidden');
            openDestFormModal(dest);
        };

        document.getElementById('details-delete-btn').onclick = async () => {
            if (confirm(`Remove "${dest.title}" from your bucket list?`)) {
                await deleteDestination(dest.id);
                detailsModal.classList.add('hidden');
            }
        };

        // Load Budget Planner
        fetchExpenses(dest.id);

        detailsModal.classList.remove('hidden');
    }

    async function changeStatusAndReload(id, status) {
        try {
            const res = await fetch(`/api/destinations/${id}/status`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status })
            });
            if (res.ok) {
                detailsModal.classList.add('hidden');
                refreshAllData();
                showToast(`Destination status changed to ${status}`, 'success');
            }
        } catch (err) {
            console.error(err);
        }
    }

    async function deleteDestination(id) {
        try {
            const res = await fetch(`/api/destinations/${id}`, { method: 'DELETE' });
            if (res.ok) {
                refreshAllData();
                showToast('Destination deleted successfully', 'info');
            }
        } catch (err) {
            console.error(err);
        }
    }

    /* ==========================================================================
       SUBMIT HANDLERS
       ========================================================================== */
    async function handleDestFormSubmit(e) {
        e.preventDefault();
        const id = document.getElementById('form-id').value;
        const payload = {
            title: document.getElementById('form-title').value.trim(),
            country: document.getElementById('form-country').value.trim(),
            city: document.getElementById('form-city').value.trim(),
            category: document.getElementById('form-category').value,
            mood: document.getElementById('form-mood').value,
            status: document.getElementById('form-status').value,
            targetDate: document.getElementById('form-target-date').value.trim(),
            budget: parseFloat(document.getElementById('form-budget').value) || null,
            bestSeason: document.getElementById('form-season').value.trim(),
            duration: document.getElementById('form-duration').value.trim(),
            latitude: parseFloat(document.getElementById('form-latitude').value) || null,
            longitude: parseFloat(document.getElementById('form-longitude').value) || null,
            rating: parseInt(document.getElementById('form-rating').value) || 5,
            imageUrl: document.getElementById('form-image-url').value.trim(),
            description: document.getElementById('form-description').value.trim()
        };

        try {
            let res;
            if (id) {
                res = await fetch(`/api/destinations/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            } else {
                res = await fetch('/api/destinations', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            }

            if (res.ok) {
                destModal.classList.add('hidden');
                refreshAllData();
                showToast(id ? 'Destination updated!' : 'Destination added successfully!', 'success');
            } else {
                showToast('Failed to save destination. Check properties.', 'danger');
            }
        } catch (err) {
            console.error(err);
            showToast('Connection failed', 'danger');
        }
    }

    async function handleGoalFormSubmit(e) {
        e.preventDefault();
        const payload = {
            title: document.getElementById('goal-title').value.trim(),
            description: document.getElementById('goal-desc').value.trim(),
            target: parseInt(document.getElementById('goal-target').value) || 5,
            completed: parseInt(document.getElementById('goal-completed').value) || 0,
            category: document.getElementById('goal-category').value.trim() || 'General'
        };

        try {
            const res = await fetch('/api/goals', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                goalModal.classList.add('hidden');
                refreshAllData();
                showToast('New goal set successfully!', 'success');
            }
        } catch (err) {
            console.error(err);
        }
    }

    async function handleMemoryFormSubmit(e) {
        e.preventDefault();
        const payload = {
            destinationId: parseInt(memoryDestSelect.value),
            caption: document.getElementById('memory-caption').value.trim(),
            imageUrl: document.getElementById('memory-image-url').value.trim(),
            memoryDate: document.getElementById('memory-date').value.trim()
        };

        try {
            const res = await fetch('/api/memories', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                memoryModal.classList.add('hidden');
                refreshAllData();
                showToast('New memory saved to the pinboard!', 'success');
            }
        } catch (err) {
            console.error(err);
        }
    }

    async function handleExpenseFormSubmit(e) {
        e.preventDefault();
        const payload = {
            destinationId: selectedDestId,
            category: document.getElementById('expense-category').value,
            estimatedAmount: parseFloat(document.getElementById('expense-estimated').value) || 0.0,
            actualAmount: parseFloat(document.getElementById('expense-actual').value) || 0.0
        };

        try {
            const res = await fetch('/api/expenses', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                expenseModal.classList.add('hidden');
                fetchExpenses(selectedDestId);
                // Refresh stats
                fetchDestinations().then(() => updateStatistics());
                showToast('Expense logged successfully', 'success');
            }
        } catch (err) {
            console.error(err);
        }
    }

    /* ==========================================================================
       LIGHT & DARK THEMES
       ========================================================================== */
    function initTheme() {
        const savedTheme = localStorage.getItem('theme') || 'dark';
        if (savedTheme === 'light') {
            document.body.classList.remove('dark-theme');
            document.body.classList.add('light-theme');
            themeToggleBtn.innerHTML = `<i class="fa-solid fa-moon"></i>`;
        } else {
            document.body.classList.remove('light-theme');
            document.body.classList.add('dark-theme');
            themeToggleBtn.innerHTML = `<i class="fa-solid fa-sun"></i>`;
        }
    }

    function toggleTheme() {
        if (document.body.classList.contains('dark-theme')) {
            document.body.classList.replace('dark-theme', 'light-theme');
            themeToggleBtn.innerHTML = `<i class="fa-solid fa-moon"></i>`;
            localStorage.setItem('theme', 'light');
        } else {
            document.body.classList.replace('light-theme', 'dark-theme');
            themeToggleBtn.innerHTML = `<i class="fa-solid fa-sun"></i>`;
            localStorage.setItem('theme', 'dark');
        }
    }

    /* ==========================================================================
       UI INTERACTIONS & UTILITIES
       ========================================================================== */
    function setupEventListeners() {
        // Theme switch
        themeToggleBtn.addEventListener('click', toggleTheme);

        // Surprise Engine click
        surpriseBtn.addEventListener('click', handleSurpriseMe);
        document.getElementById('surprise-close-btn').addEventListener('click', () => surpriseModal.classList.add('hidden'));

        // Destinations Open / Close form
        addDestBtn.addEventListener('click', () => openDestFormModal());
        emptyAddBtn.addEventListener('click', () => openDestFormModal());
        document.getElementById('modal-close-btn').addEventListener('click', () => destModal.classList.add('hidden'));
        document.getElementById('modal-cancel-btn').addEventListener('click', () => destModal.classList.add('hidden'));
        destForm.addEventListener('submit', handleDestFormSubmit);

        // Details Modal Close
        document.getElementById('details-close-btn').addEventListener('click', () => detailsModal.classList.add('hidden'));

        // Goals Modal Open / Close / Save
        addGoalBtn.addEventListener('click', () => {
            goalForm.reset();
            goalModal.classList.remove('hidden');
        });
        document.getElementById('goal-close-btn').addEventListener('click', () => goalModal.classList.add('hidden'));
        document.getElementById('goal-cancel-btn').addEventListener('click', () => goalModal.classList.add('hidden'));
        goalForm.addEventListener('submit', handleGoalFormSubmit);

        // Memories Modal Open / Close / Save
        addMemoryBtn.addEventListener('click', () => {
            memoryForm.reset();
            populateMemoryDestinationDropdown();
            memoryModal.classList.remove('hidden');
        });
        document.getElementById('memory-close-btn').addEventListener('click', () => memoryModal.classList.add('hidden'));
        document.getElementById('memory-cancel-btn').addEventListener('click', () => memoryModal.classList.add('hidden'));
        memoryForm.addEventListener('submit', handleMemoryFormSubmit);

        // Expense Modal Open / Close / Save
        addExpenseBtn.addEventListener('click', () => {
            expenseForm.reset();
            document.getElementById('expense-dest-id').value = selectedDestId;
            expenseModal.classList.remove('hidden');
        });
        document.getElementById('expense-close-btn').addEventListener('click', () => expenseModal.classList.add('hidden'));
        document.getElementById('expense-cancel-btn').addEventListener('click', () => expenseModal.classList.add('hidden'));
        expenseForm.addEventListener('submit', handleExpenseFormSubmit);

        // Filter Tabs (Wishlist, Planned, Visited)
        tabButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                tabButtons.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                currentStatusFilter = btn.dataset.status;
                renderGrid();
            });
        });

        // Category Select Filter
        categorySelect.addEventListener('change', (e) => {
            currentCategoryFilter = e.target.value;
            renderGrid();
        });

        // Mood Discovery selector cards
        document.querySelectorAll('.mood-card').forEach(card => {
            card.addEventListener('click', () => {
                document.querySelectorAll('.mood-card').forEach(c => c.classList.remove('active'));
                card.classList.add('active');
                currentMoodFilter = card.dataset.mood;
                renderGrid();
            });
        });

        // Search Input box query search
        let debounceTimer;
        searchInput.addEventListener('input', (e) => {
            searchQuery = e.target.value.trim();
            clearSearchBtn.hidden = !searchQuery;
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                renderGrid();
            }, 300);
        });

        clearSearchBtn.addEventListener('click', () => {
            searchInput.value = '';
            searchQuery = '';
            clearSearchBtn.hidden = true;
            renderGrid();
        });
    }

    function attachCardListeners() {
        // Details Open Journey Link
        document.querySelectorAll('.journey-link').forEach(link => {
            link.addEventListener('click', () => {
                const id = link.dataset.id;
                const dest = destinations.find(d => d.id == id);
                if (dest) openDetailsModal(dest);
            });
        });

        // Heart Wishlist Status toggler
        document.querySelectorAll('.wishlist-toggle-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                e.stopPropagation();
                const id = btn.dataset.id;
                const dest = destinations.find(d => d.id == id);
                if (dest) {
                    const newStatus = dest.status === 'WISHLIST' ? 'PLANNED' : 'WISHLIST';
                    await changeStatusAndReload(id, newStatus);
                }
            });
        });
    }

    function populateMemoryDestinationDropdown() {
        if (destinations.length === 0) {
            memoryDestSelect.innerHTML = `<option value="">No destinations added yet</option>`;
            return;
        }
        memoryDestSelect.innerHTML = destinations.map(dest => `
            <option value="${dest.id}">${escapeHTML(dest.title)} (${escapeHTML(dest.country)})</option>
        `).join('');
    }

    function showToast(msg, type = 'info') {
        const toast = document.createElement('div');
        const icon = type === 'success' ? 'fa-circle-check' : (type === 'danger' ? 'fa-circle-exclamation' : 'fa-circle-info');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${escapeHTML(msg)}</span>`;
        toastContainer.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(10px) scale(0.95)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 3200);
    }

    function escapeHTML(str) {
        if (!str) return '';
        return str.replace(/[&<>'"]/g, 
            tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
        );
    }
});

// Helper globally scoped function to handle sticky headers click
function scrollToSection(id) {
    const el = document.getElementById(id);
    if (el) {
        el.scrollIntoView({ behavior: 'smooth' });
    }
}
window.scrollToSection = scrollToSection;
