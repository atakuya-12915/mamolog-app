// 仮データ
/* const tasks = [
  { id: 1, title: "買い物に行く", assignee: "ぱぱ", tag: "家事", due: "今日", done: false },
  { id: 2, title: "お弁当の仕込み", assignee: "まま", tag: "料理", due: "今日", done: false },
  { id: 3, title: "保育園の準備", assignee: "まま", tag: "育児", due: "今日", done: true },
];

function renderTasks() {
  const taskList = document.getElementById("taskList");
  const completedList = document.getElementById("completedList");
  taskList.innerHTML = "";
  completedList.innerHTML = "";
  let remaining = 0;

  tasks.forEach(task => {
	const card = document.createElement("div");
	card.className = "task-card" + (task.done ? " done" : "");
	card.innerHTML = `
	  <div class="task-main">
		<input type="checkbox" ${task.done ? "checked" : ""} onchange="toggleTask(${task.id})">
		<div>
		  <a href="/todos/edit/${task.id}" class="task-title">${task.title}</a>
		  <div class="task-meta">${task.assignee} | ${task.tag} | ${task.due}</div>
		</div>
	  </div>
	  <div class="avatar">${task.assignee === "ぱぱ" ? "👨" : "👩"}</div>
	  <div class="task-actions">
		<a href="/todos${task.id}/edit" class="btn small">編集</a>
	  </div>
	`;

	if (task.done) {
	  completedList.appendChild(card);
	} else {
	  taskList.appendChild(card);
	  remaining++;
	}
  });

  document.getElementById("remainingCount").innerText = remaining;
}

// タスク完了切り替え
function toggleTask(id) {
  const task = tasks.find(t => t.id === id);
  if (task) {
	task.done = !task.done;
	renderTasks();
  }
}

renderTasks(); */

// ハンバーガーメニュー
const menuToggle = document.querySelector("#menu-toggle");
const sideMenu = document.querySelector("#side-menu");
const overlay = document.createElement("div");
overlay.id = "overlay";
document.body.appendChild(overlay);

// サイドメニューの開閉
document.getElementById('menu-toggle').addEventListener('click', function() {
	document.getElementById('side-menu').classList.toggle('open');
});

menuToggle.addEventListener("click", () => {
	sideMenu.classList.toggle("active");
	overlay.classList.toggle("active");
});

document.querySelectorAll(".nav-link").forEach(link => {
	link.addEventListener("click", e => {
		const href = link.getAttribute("href");
		if (href.startsWith("#")) e.preventDefault();
		sideMenu.classList.remove("active");
		overlay.classList.remove("active");
	});
});

overlay.addEventListener("click", () => {
	sideMenu.classList.remove("active");
	overlay.classList.remove("active");
});

// タグボタンクリックで input に反映
const tagBtns = document.querySelectorAll('.tag-btn');
const tagInput = document.getElementById('tagInput');

tagBtns.forEach(btn => {
	if (btn.dataset.tag === tagInput.value) {
		btn.classList.add('selected'); // 初期値を選択
	}

	btn.addEventListener('click', () => {
		tagBtns.forEach(b => b.classList.remove('selected'));
		btn.classList.add('selected');
		tagInput.value = btn.dataset.tag;
	});
});

chk.addEventListener("change", async (e) => {
	const id = e.target.getAttribute("data-id");
	try {
		const response = await fetch(`/todos/${id}/toggle`, { method: "POST" });
		if (!response.ok) throw new Error("Network response was not ok");
		const data = await response.json();
		const card = e.target.closest(".task-card");
		if (data.completed) {
			card.classList.add("done");
		} else {
			card.classList.remove("done");
		}
	} catch (err) {
		alert("更新に失敗しました");
		e.target.checked = !e.target.checked;
		console.error(err);
	}
});
