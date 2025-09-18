document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("categoryModal");
    const openBtn = document.getElementById("openModal");
    const closeBtn = document.getElementById("closeModal");
    const form = document.getElementById("categoryForm");

    openBtn.addEventListener("click", () => modal.style.display = "flex");
    closeBtn.addEventListener("click", () => modal.style.display = "none");
    window.addEventListener("click", e => {
        if (e.target === modal) modal.style.display = "none";
    });

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const name = document.getElementById("newCategoryName").value.trim();
        if (!name) return;

        const response = await fetch("/categories/add", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({ newCategoryName: name })
        });

        if (response.ok) {
            // 成功したら閉じる
            document.getElementById("newCategoryName").value = "";
            modal.style.display = "none";
        } else {
            alert("カテゴリの追加に失敗しました");
        }
    });
});
