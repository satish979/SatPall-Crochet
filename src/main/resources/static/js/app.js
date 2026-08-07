const CONTEXT_PATH = '/Loomellecrochet';

document.addEventListener('DOMContentLoaded', function () {
    document.body.classList.add('page-loading');

    initSkeletonLoader();
    initNavbarActiveLink();
    initSpinner();
    initImagePreview();
    initSearchSuggestions();
    initQuantityButtons();
    initTooltips();
    initToastContainers();
    initButtons();
    initLazyImages();
    initScrollReveal();
    initDashboardStats();
    initProgressBars();

    initCartActions();
});

function initNavbarActiveLink() {
    const path = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach((link) => {
        const href = link.getAttribute('href');
        if (href && href !== '#' && path.includes(href.replace('/', ''))) {
            link.classList.add('active');
        }
    });
}

function initSpinner() {
    const spinner = document.getElementById('pageSpinner');
    if (!spinner) return;

    window.showSpinner = function () {
        spinner.classList.add('show');
    };

    window.hideSpinner = function () {
        spinner.classList.remove('show');
    };
}

function initImagePreview() {
    const input = document.querySelector('[data-image-input]');
    const preview = document.querySelector('[data-image-preview]');
    if (!input || !preview) return;

    input.addEventListener('change', function () {
        const file = this.files && this.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.classList.remove('d-none');
        };
        reader.readAsDataURL(file);
    });
}

function initSearchSuggestions() {
    const input = document.querySelector('[data-search-input]');
    const box = document.querySelector('[data-search-suggestions]');
    if (!input || !box) return;

    let timer = null;

    input.addEventListener('input', function () {
        const keyword = this.value.trim();
        clearTimeout(timer);

        if (keyword.length < 2) {
            box.innerHTML = '';
            box.classList.add('d-none');
            return;
        }

        timer = setTimeout(function () {
            fetch(`/search/suggestions?keyword=${encodeURIComponent(keyword)}`)
                .then((res) => res.json())
                .then((items) => {
                    box.innerHTML = '';
                    if (!items || items.length === 0) {
                        box.classList.add('d-none');
                        return;
                    }

                    items.forEach((item) => {
                        const el = document.createElement('a');
                        el.href = `/products/${item.id}`;
                        el.className = 'dropdown-item';
                        el.textContent = item.name;
                        box.appendChild(el);
                    });

                    box.classList.remove('d-none');
                })
                .catch(() => {
                    box.classList.add('d-none');
                });
        }, 250);
    });

    document.addEventListener('click', function (e) {
        if (!box.contains(e.target) && e.target !== input) {
            box.classList.add('d-none');
        }
    });
}

function initQuantityButtons() {
    document.querySelectorAll('[data-qty-plus]').forEach((btn) => {
        btn.addEventListener('click', function () {
            const target = document.querySelector(this.dataset.qtyPlus);
            if (!target) return;
            target.value = parseInt(target.value || '1', 10) + 1;
            target.dispatchEvent(new Event('change'));
        });
    });

    document.querySelectorAll('[data-qty-minus]').forEach((btn) => {
        btn.addEventListener('click', function () {
            const target = document.querySelector(this.dataset.qtyMinus);
            if (!target) return;
            const value = parseInt(target.value || '1', 10);
            if (value > 1) {
                target.value = value - 1;
                target.dispatchEvent(new Event('change'));
            }
        });
    });
}

function initTooltips() {
    if (window.bootstrap) {
        document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach((el) => {
            new bootstrap.Tooltip(el);
        });
    }
}

function initToastContainers() {
    window.showToast = function (message, type = 'success') {
        let container = document.querySelector('.toast-container');
        if (!container) {
            container = document.createElement('div');
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `alert alert-${type} shadow`;
        toast.style.minWidth = '280px';
        toast.innerHTML = message;
        container.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 3000);
    };
}

function initButtons() {
    document.querySelectorAll('button, .btn').forEach((button) => {
        button.addEventListener('click', function (event) {
            if (button.tagName === 'A' || button.classList.contains('dropdown-toggle')) return;
            const ripple = document.createElement('span');
            ripple.className = 'ripple';
            const rect = button.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height) * 1.1;
            ripple.style.width = `${size}px`;
            ripple.style.height = `${size}px`;
            ripple.style.left = `${event.clientX - rect.left}px`;
            ripple.style.top = `${event.clientY - rect.top}px`;
            button.appendChild(ripple);
            setTimeout(() => ripple.remove(), 700);
            button.classList.add('is-loading');
            setTimeout(() => button.classList.remove('is-loading'), 850);
        });
    });
}

function initLazyImages() {
    const placeholder = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="640" height="480" viewBox="0 0 640 480"%3E%3Cdefs%3E%3ClinearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%"%3E%3Cstop offset="0%" stop-color="%23f6ecee"/%3E%3Cstop offset="100%" stop-color="%23efe3e6"/%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width="640" height="480" fill="url(%23g)"/%3E%3Ccircle cx="160" cy="200" r="70" fill="%23ffffff" fill-opacity="0.3"/%3E%3Crect x="130" y="300" width="380" height="24" rx="12" fill="%23ffffff" fill-opacity="0.3"/%3E%3C/svg%3E';

    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            const img = entry.target;
            obs.unobserve(img);
            revealImage(img, placeholder);
        });
    }, { rootMargin: '120px 0px' });

    document.querySelectorAll('img').forEach((img) => {
        if (img.closest('.image-shell') || img.classList.contains('lazy-image')) return;

        const actualSrc = img.getAttribute('src');
        const shell = document.createElement('div');
        shell.className = 'image-shell';
        img.parentNode.insertBefore(shell, img);
        shell.appendChild(img);

        img.classList.add('lazy-image');
        img.setAttribute('loading', 'lazy');
        img.setAttribute('decoding', 'async');
        img.setAttribute('src', placeholder);
        img.setAttribute('data-actual-src', actualSrc || '');
        img.setAttribute('alt', img.getAttribute('alt') || 'Product image');

        if (img.complete && img.naturalWidth > 0) {
            revealImage(img, placeholder);
        } else {
            observer.observe(img);
        }
    });
}

function revealImage(img, placeholder) {
    if (img.dataset.revealed === 'true') return;
    img.dataset.revealed = 'true';
    const actualSrc = img.getAttribute('data-actual-src') || img.getAttribute('src');
    const shell = img.closest('.image-shell');

    if (actualSrc && actualSrc !== placeholder) {
        img.setAttribute('src', actualSrc);
    }

    img.addEventListener('load', function () {
        if (shell) {
            shell.classList.add('is-loaded');
        }
        img.classList.add('is-loaded');
    });

    if (img.complete && img.naturalWidth > 0) {
        if (shell) shell.classList.add('is-loaded');
        img.classList.add('is-loaded');
    }
}

function initScrollReveal() {
    document.querySelectorAll('.hero-title, .hero-subtitle, .section-title, .section-subtitle, .card-soft, .product-card, .category-card, .footer, .dashboard-stat-card, .dashboard-action, .table, .form-card, .page-section, .btn').forEach((el, index) => {
        el.classList.add('reveal-on-scroll');
        el.style.setProperty('--reveal-delay', `${index * 70}ms`);
    });

    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            entry.target.classList.add('is-visible');
            obs.unobserve(entry.target);
        });
    }, { threshold: 0.12 });

    document.querySelectorAll('.reveal-on-scroll').forEach((el) => observer.observe(el));
}

function initDashboardStats() {
    document.querySelectorAll('[data-count]').forEach((el, index) => {
        const card = el.closest('.dashboard-stat-card');
        if (card) {
            card.classList.add('reveal-on-scroll');
            card.style.setProperty('--reveal-delay', `${index * 90}ms`);
        }
        const target = parseFloat(el.dataset.count || '0');
        const prefix = el.dataset.prefix || '';
        const suffix = el.dataset.suffix || '';
        const decimals = parseInt(el.dataset.decimals || '0', 10);
        if (!Number.isFinite(target)) return;

        const duration = 1200;
        const start = performance.now();
        const step = (now) => {
            const progress = Math.min((now - start) / duration, 1);
            const eased = 1 - Math.pow(1 - progress, 3);
            const current = target * eased;
            const formatted = `${prefix}${current.toFixed(decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ',')}${suffix}`;
            el.textContent = formatted;
            if (progress < 1) requestAnimationFrame(step);
        };
        requestAnimationFrame(step);
    });
}

function initProgressBars() {
    document.querySelectorAll('.progress-bar[data-progress]').forEach((bar) => {
        const fill = bar.querySelector('span');
        if (!fill) return;
        const target = parseFloat(bar.dataset.progress || '0');
        requestAnimationFrame(() => {
            fill.style.width = `${Math.max(0, Math.min(100, target))}%`;
        });
    });
}

function initCartActions() {
    const addBtn = document.getElementById('addToCartBtn');
    const buyBtn = document.getElementById('buyNowBtn');
    const wishlistBtn = document.getElementById('wishlistBtn');

    if (addBtn) {
        addBtn.addEventListener('click', function () {
            const productId = this.dataset.productId;
            const qtyInput = document.getElementById('qty');
            const qty = qtyInput ? qtyInput.value : '1';

            addBtn.disabled = true;

            fetch(CONTEXT_PATH + '/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({ productId: productId, quantity: qty })
            })
                .then(async response => {
                    const text = await response.text();
                    if (!response.ok) throw new Error('HTTP ' + response.status);
                    if (text.trim().startsWith('<')) throw new Error('Server returned HTML instead of JSON');

                    const data = JSON.parse(text);
                    showCartToast(data.message || 'Added to cart');
                    updateCartBadge(data.cartCount);
                })
                .catch(error => {
                    console.error(error);
                    showToast(error.message, 'danger');
                })
                .finally(() => {
                    addBtn.disabled = false;
                });
        });
    }

    if (buyBtn) {
        buyBtn.addEventListener('click', function () {
            const productId = this.dataset.productId;
            const qtyInput = document.getElementById('qty');
            const qty = qtyInput ? qtyInput.value : '1';

            fetch(CONTEXT_PATH + '/api/cart/buy-now', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: qty
                })
            }).then(() => {
                window.location.href = '/checkout';
            });
        });
    }

    if (wishlistBtn) {
        wishlistBtn.addEventListener('click', function () {
            const productId = this.dataset.productId;

            fetch(CONTEXT_PATH + '/api/wishlist/add/' + productId, {
                method: 'POST'
            })
                .then(res => res.json())
                .then(() => {
                    this.classList.toggle('active');
                    showToast('Wishlist updated.');
                });
        });
    }
}

function showCartToast(message) {
    const toastEl = document.getElementById('cartToast');
    document.getElementById('cartToastMsg').textContent = message || 'Added to cart';
    const toast = bootstrap.Toast.getOrCreateInstance(toastEl, { delay: 3500 });
    toast.show();
}

function updateCartBadge(count) {
    const badge = document.getElementById('cartCountBadge');
    if (badge && typeof count === 'number') {
        badge.textContent = count;
        badge.classList.remove('d-none');
    }
}

function initSkeletonLoader() {
    const loader = document.createElement('div');
    loader.className = 'page-loader';
    loader.innerHTML = `
        <div class="loader-card">
            <div class="d-flex align-items-center gap-3 mb-4">
                <div class="skeleton-circle" style="width: 48px; height: 48px;"></div>
                <div class="flex-grow-1">
                    <div class="skeleton-line mb-2" style="height: 14px; width: 55%;"></div>
                    <div class="skeleton-line" style="height: 12px; width: 80%;"></div>
                </div>
            </div>
            <div class="skeleton-block mb-3" style="height: 140px;"></div>
            <div class="row g-3">
                <div class="col-6"><div class="skeleton-block" style="height: 96px;"></div></div>
                <div class="col-6"><div class="skeleton-block" style="height: 96px;"></div></div>
                <div class="col-12"><div class="skeleton-block" style="height: 48px;"></div></div>
            </div>
        </div>
    `;
    document.body.appendChild(loader);

    window.addEventListener('load', () => {
        setTimeout(() => {
            loader.classList.add('is-hidden');
            document.body.classList.remove('page-loading');
            document.body.classList.add('page-ready');
            setTimeout(() => loader.remove(), 350);
        }, 400);
    });
}