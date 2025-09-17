document.addEventListener("DOMContentLoaded", () => {
  const categoryList = document.getElementById("categoryList");
  const categories = [
    { id: 1, name: "家事", color: "#ffcc00" },
    { id: 2, name: "育児", color: "#ff99cc" },
    { id: 3, name: "仕事", color: "#99ccff" }
  ];

  function renderCategories() {
    categoryList.innerHTML = "";
    categories.forEach(cat => {
      const li = document.createElement("li");
      li.className = "category-item";
      li.innerHTML = `
        <span class="cat-color" style="background:${cat.color}"></span>
        <span class="cat-name">${cat.name}</span>
        <button class="btn small danger" onclick="deleteCategory(${cat.id})">削除</button>
      `;
      categoryList.appendChild(li);
    });
  }

  window.deleteCategory = (id) => {
    const index = categories.findIndex(c => c.id === id);
    if (index > -1) {
      categories.splice(index, 1);
      renderCategories();
    }
  };

  document.getElementById("addCategoryBtn").addEventListener("click", () => {
    const name = document.getElementById("newCategoryInput").value.trim();
    const color = document.getElementById("newCategoryColor").value;
    if (!name) return alert("カテゴリ名を入力してください");
    categories.push({ id: Date.now(), name, color });
    document.getElementById("newCategoryInput").value = "";
    renderCategories();
  });

  renderCategories();
});
