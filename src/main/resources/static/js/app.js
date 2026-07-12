CONTEXT_PATH = '/SatPall-Crochet';

document.addEventListener("DOMContentLoaded", function() {

    initNavbarActiveLink();
    initSpinner();
    initImagePreview();
    initSearchSuggestions();
    initQuantityButtons();
    initTooltips();
    initToastContainers();

    // Product & Cart
    initCartActions();

});

function initNavbarActiveLink() {
    const path = window.location.pathname;
    document.querySelectorAll(".nav-link").forEach((link) => {
        const href = link.getAttribute("href");
        if (href && href !== "#" && path.includes(href.replace("/", ""))) {
            link.classList.add("active");
        }
    });
}

function initSpinner() {
    const spinner = document.getElementById("pageSpinner");
    if (!spinner) return;

    window.showSpinner = function() {
        spinner.classList.add("show");
    };

    window.hideSpinner = function() {
        spinner.classList.remove("show");
    };
}

function initImagePreview() {
    const input = document.querySelector("[data-image-input]");
    const preview = document.querySelector("[data-image-preview]");
    if (!input || !preview) return;

    input.addEventListener("change", function() {
        const file = this.files && this.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.classList.remove("d-none");
        };
        reader.readAsDataURL(file);
    });
}

function initSearchSuggestions() {
    const input = document.querySelector("[data-search-input]");
    const box = document.querySelector("[data-search-suggestions]");
    if (!input || !box) return;

    let timer = null;

    input.addEventListener("input", function() {
        const keyword = this.value.trim();
        clearTimeout(timer);

        if (keyword.length < 2) {
            box.innerHTML = "";
            box.classList.add("d-none");
            return;
        }

        timer = setTimeout(function() {
            fetch(`/search/suggestions?keyword=${encodeURIComponent(keyword)}`)
                .then((res) => res.json())
                .then((items) => {
                    box.innerHTML = "";
                    if (!items || items.length === 0) {
                        box.classList.add("d-none");
                        return;
                    }

                    items.forEach((item) => {
                        const el = document.createElement("a");
                        el.href = `/products/${item.id}`;
                        el.className = "dropdown-item";
                        el.textContent = item.name;
                        box.appendChild(el);
                    });

                    box.classList.remove("d-none");
                })
                .catch(() => {
                    box.classList.add("d-none");
                });
        }, 250);
    });

    document.addEventListener("click", function(e) {
        if (!box.contains(e.target) && e.target !== input) {
            box.classList.add("d-none");
        }
    });
}

function initQuantityButtons() {
    document.querySelectorAll("[data-qty-plus]").forEach((btn) => {
        btn.addEventListener("click", function() {
            const target = document.querySelector(this.dataset.qtyPlus);
            if (!target) return;
            target.value = parseInt(target.value || "1", 10) + 1;
            target.dispatchEvent(new Event("change"));
        });
    });

    document.querySelectorAll("[data-qty-minus]").forEach((btn) => {
        btn.addEventListener("click", function() {
            const target = document.querySelector(this.dataset.qtyMinus);
            if (!target) return;
            const value = parseInt(target.value || "1", 10);
            if (value > 1) {
                target.value = value - 1;
                target.dispatchEvent(new Event("change"));
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
    window.showToast = function(message, type = "success") {
        let container = document.querySelector(".toast-container");
        if (!container) {
            container = document.createElement("div");
            container.className = "toast-container";
            document.body.appendChild(container);
        }

        const toast = document.createElement("div");
        toast.className = `alert alert-${type} shadow`;
        toast.style.minWidth = "280px";
        toast.innerHTML = message;
        container.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 3000);
    };
}
function initCartActions() {

    const addBtn = document.getElementById("addToCartBtn");
    const buyBtn = document.getElementById("buyNowBtn");
    const wishlistBtn = document.getElementById("wishlistBtn");

    if (addBtn) {

        addBtn.addEventListener("click", function() {

            const productId = this.dataset.productId;
            const qty = document.getElementById("qty").value;

            fetch(CONTEXT_PATH + "/api/cart/add", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: qty
                })
            })
                .then(async response => {

                    const text = await response.text();

                    if (!response.ok) {
                        throw new Error("HTTP " + response.status);
                    }

                    if (text.trim().startsWith("<")) {
                        throw new Error("Server returned HTML instead of JSON");
                    }

                    const data = JSON.parse(text);

                    showToast(data.message);

                })
                .catch(error => {
                    console.error(error);
                    showToast(error.message, "danger");
                });

        });

    }


    if (buyBtn) {

        buyBtn.addEventListener("click", function() {

            const productId = this.dataset.productId;
            const qty = document.getElementById("qty").value;

            fetch(CONTEXT_PATH + "/api/cart/buy-now", {

                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: qty
                })
            })
                .then(() => {

                    window.location.href = "/checkout";

                });

        });

    }

    if (wishlistBtn) {

        wishlistBtn.addEventListener("click", function() {

            const productId = this.dataset.productId;

            fetch(CONTEXT_PATH + "/api/wishlist/add/" + productId, {

                method: "POST"
            })
                .then(res => res.json())
                .then(() => {

                    this.classList.toggle("active");

                    showToast("Wishlist updated.");

                });

        });

    }

}