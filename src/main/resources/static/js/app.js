const CONTEXT_PATH = '/Loomellecrochet';

document.addEventListener('DOMContentLoaded', function() {
    initNavbarActiveLink();
    initSpinner();
    initImagePreview();
    initSearchSuggestions();
    initQuantityButtons();
    initTooltips();
    initToastContainers();
    initButtons();
    initScrollReveal();
    initDashboardStats();
    initProgressBars();
    initRazorpayScript();
    initCartActions();
});

function initNavbarActiveLink() {
    const path = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach((link) => {
        const href = link.getAttribute('href');
        if (href && href !== '#' && path.includes(href.replace('/', '')) && href !== '/' && href !== CONTEXT_PATH + '/') {
            link.classList.add('active');
        }
    });
}

function initSpinner() {
    const spinner = document.getElementById('pageSpinner');
    if (!spinner) return;

    window.showSpinner = function() {
        spinner.classList.add('show');
    };

    window.hideSpinner = function() {
        spinner.classList.remove('show');
    };
}

function initImagePreview() {
    const input = document.querySelector('[data-image-input]');
    const preview = document.querySelector('[data-image-preview]');
    if (!input || !preview) return;

    input.addEventListener('change', function() {
        const file = this.files && this.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function(e) {
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

    input.addEventListener('input', function() {
        const keyword = this.value.trim();
        clearTimeout(timer);

        if (keyword.length < 2) {
            box.innerHTML = '';
            box.classList.add('d-none');
            return;
        }

        timer = setTimeout(function() {
            fetch(CONTEXT_PATH + `/search/suggestions?keyword=${encodeURIComponent(keyword)}`)
                .then((res) => res.json())
                .then((items) => {
                    box.innerHTML = '';
                    if (!items || items.length === 0) {
                        box.classList.add('d-none');
                        return;
                    }

                    items.forEach((item) => {
                        const el = document.createElement('a');
                        el.href = `${CONTEXT_PATH}/product-details/${item.id}`;
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

    document.addEventListener('click', function(e) {
        if (!box.contains(e.target) && e.target !== input) {
            box.classList.add('d-none');
        }
    });
}

function initQuantityButtons() {
    document.querySelectorAll('[data-qty-plus]').forEach((btn) => {
        btn.addEventListener('click', function() {
            const target = document.querySelector(this.dataset.qtyPlus);
            if (!target) return;
            target.value = parseInt(target.value || '1', 10) + 1;
            target.dispatchEvent(new Event('change'));
        });
    });

    document.querySelectorAll('[data-qty-minus]').forEach((btn) => {
        btn.addEventListener('click', function() {
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
    if (window.bootstrap && bootstrap.Tooltip) {
        document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach((el) => {
            new bootstrap.Tooltip(el);
        });
    }
}

function initToastContainers() {
    window.showToast = function(message, type = 'success') {
        let container = document.querySelector('.toast-container');
        if (!container) {
            container = document.createElement('div');
            container.className = 'toast-container position-fixed top-0 end-0 p-3';
            container.style.zIndex = '1090';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast luxury-toast align-items-center show mb-2 border-0 shadow-lg`;
        toast.innerHTML = `
            <div class="d-flex align-items-center p-2">
                <div class="toast-icon-wrap me-2">
                    <i class="fa-solid ${type === 'danger' ? 'fa-circle-exclamation text-danger' : 'fa-circle-check text-success'}"></i>
                </div>
                <div class="toast-body flex-grow-1 p-1">
                    <span class="fw-semibold">${message}</span>
                </div>
                <button type="button" class="btn-close me-2 m-auto" onclick="this.closest('.toast').remove()"></button>
            </div>
        `;
        container.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 3500);
    };
}

function initButtons() {
    document.querySelectorAll('button, .btn').forEach((button) => {
        button.addEventListener('click', function(event) {
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
        });
    });
}

function initScrollReveal() {
    const revealElements = document.querySelectorAll('.card-soft, .product-card, .category-card, .footer-perk-item, .dashboard-stat-card');
    if (!('IntersectionObserver' in window)) return;

    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            entry.target.classList.add('is-visible');
            obs.unobserve(entry.target);
        });
    }, { threshold: 0.1 });

    revealElements.forEach((el) => observer.observe(el));
}

function initDashboardStats() {
    document.querySelectorAll('[data-count]').forEach((el) => {
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
        const fill = bar.querySelector('span') || bar;
        const target = parseFloat(bar.dataset.progress || '0');
        requestAnimationFrame(() => {
            fill.style.width = `${Math.max(0, Math.min(100, target))}%`;
        });
    });
}

function initCartActions() {
    const addBtn = document.getElementById('addToCartBtn');
    const buyBtn = document.getElementById('buyNowBtn');
    const wishlistBtns = document.querySelectorAll('.wishlist-btn');

    if (addBtn) {
        addBtn.addEventListener('click', function() {
            const productId = this.dataset.productId;
            const qtyInput = document.getElementById('qty');
            const qty = qtyInput ? qtyInput.value : '1';

            addBtn.disabled = true;
            const originalHtml = addBtn.innerHTML;
            addBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Adding...';

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
                    let data;
                    try { data = JSON.parse(text); } catch(e) { throw new Error('Invalid server response'); }

                    addBtn.innerHTML = '<i class="fa-solid fa-check me-1"></i> Added!';
                    setTimeout(() => {
                        addBtn.innerHTML = originalHtml;
                        addBtn.disabled = false;
                    }, 2000);

                    showCartToast(data.message || 'Added to cart successfully');
                    updateCartBadge(data.cartCount);
                })
                .catch(error => {
                    console.error(error);
                    addBtn.innerHTML = originalHtml;
                    addBtn.disabled = false;
                    showToast(error.message, 'danger');
                });
        });
    }

    if (buyBtn) {
        buyBtn.addEventListener('click', function() {
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
                window.location.href = CONTEXT_PATH + '/checkout';
            });
        });
    }

    wishlistBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            const productId = this.dataset.productId;
            if (!productId) return;

            fetch(CONTEXT_PATH + '/api/wishlist/add/' + productId, {
                method: 'POST'
            })
                .then(res => res.json())
                .then(() => {
                    this.classList.toggle('active');
                    const icon = this.querySelector('i');
                    if (icon) {
                        if (this.classList.contains('active')) {
                            icon.classList.remove('fa-regular');
                            icon.classList.add('fa-solid');
                        } else {
                            icon.classList.remove('fa-solid');
                            icon.classList.add('fa-regular');
                        }
                    }
                    showToast('Wishlist updated.');
                })
                .catch(() => {
                    this.classList.toggle('active');
                });
        });
    });
}

function showCartToast(message) {
    const toastEl = document.getElementById("cartToast");
    const toastMsgEl = document.getElementById("cartToastMsg");
    if (toastMsgEl) {
        toastMsgEl.textContent = message || 'Added to cart';
    }
    if (!toastEl) {
        window.showToast(message, 'success');
        return;
    }
    const toast = bootstrap.Toast.getOrCreateInstance(toastEl, { delay: 3500 });
    toast.show();
}

function updateCartBadge(count) {
    const badge = document.getElementById('cartCountBadge');
    const mobileBadge = document.getElementById('mobileCartCount');
    if (typeof count === 'number') {
        if (badge) {
            badge.textContent = count;
            if (count > 0) badge.classList.remove('d-none');
        }
        if (mobileBadge) {
            mobileBadge.textContent = count;
            if (count > 0) mobileBadge.classList.remove('d-none');
        }
    }
}

function initRazorpayScript() {
    if (document.querySelector("input[name='paymentMethod'][value='RAZORPAY']")) {
        if (!document.getElementById("razorpay-script")) {
            const script = document.createElement("script");
            script.id = "razorpay-script";
            script.src = "https://checkout.razorpay.com/v1/checkout.js";
            script.async = true;
            document.head.appendChild(script);
        }
    }
}

function sendOtp(identifier) {
    return fetch(CONTEXT_PATH + "/api/customer/auth/send-otp", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({identifier: identifier, type: "EMAIL"})
    })
    .then(async res => {
        const text = await res.text();
        let result;
        try { result = JSON.parse(text); } catch(e) { result = {success: false, message: text}; }
        return result;
    });
}

function verifyOtp(identifier, otp) {
    return fetch(CONTEXT_PATH + "/api/customer/auth/verify-otp", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({identifier: identifier, otp: otp})
    })
    .then(async res => {
        const text = await res.text();
        let result;
        try { result = JSON.parse(text); } catch(e) { result = {success: false, message: text}; }
        return result;
    });
}

function logoutCustomer() {
    return fetch(CONTEXT_PATH + "/api/customer/auth/logout", {
        method: "POST",
        headers: {"Content-Type": "application/json"}
    })
    .then(async res => {
        const text = await res.text();
        let result;
        try { result = JSON.parse(text); } catch(e) { result = {success: false, message: text}; }
        return result;
    });
}

function handleNavbarLogout() {
    logoutCustomer().then(result => {
        if (result.success) {
            showToast("Logged out successfully", "success");
            window.location.href = CONTEXT_PATH + "/";
        } else {
            showToast(result.message || "Logout failed", "danger");
        }
    });
}

function fillAddress(address) {
    const el1 = document.getElementById("addressLine1");
    if (el1) el1.value = address.addressLine1 || "";
    const addr2 = document.getElementById("addressLine2");
    if (addr2) addr2.value = address.addressLine2 || "";
    const city = document.getElementById("city");
    if (city) city.value = address.city || "";
    const state = document.getElementById("state");
    if (state) state.value = address.state || "";
    const pin = document.getElementById("pinCode");
    if (pin) pin.value = address.pinCode || "";
}
