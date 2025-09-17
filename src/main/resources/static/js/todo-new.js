document.addEventListener("DOMContentLoaded", () => {
  // タグボタン選択処理
  document.querySelectorAll(".tag-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      btn.classList.toggle("active");
    });
  });

  // フォーム送信（仮処理）
  document.getElementById("taskForm").addEventListener("submit", (e) => {
    e.preventDefault();
    alert("タスクが登録されました！（仮処理）");
    window.location.href = "index.html";
  });
});
