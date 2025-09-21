// =========================
// app.js
// 役割：
// - チェックボックスで未完了/完了を切替
// - DOM上でカードを移動して件数を更新
// - 「編集」リンクは移動後も常に有効
// - ハンバーガーメニュー操作
// =========================

// -------------------------
// ハンバーガーメニュー
// -------------------------
const menuToggle = document.querySelector("#menu-toggle");
const sideMenu = document.querySelector("#side-menu");
const overlay = document.createElement("div");
overlay.id = "overlay";
document.body.appendChild(overlay);

// メニュー開閉
menuToggle.addEventListener("click", () => {
	sideMenu.classList.toggle("active");
	overlay.classList.toggle("active");
});

// メニュー内リンククリックで閉じる
document.querySelectorAll(".nav-link").forEach(link => {
	link.addEventListener("click", e => {
		const href = link.getAttribute("href");
		if (href.startsWith("#")) e.preventDefault();
		sideMenu.classList.remove("active");
		overlay.classList.remove("active");
	});
});

// overlayクリックで閉じる
overlay.addEventListener("click", () => {
	sideMenu.classList.remove("active");
	overlay.classList.remove("active");
});

// -------------------------
// タグボタン選択
// -------------------------
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

// -------------------------
// ヘルパー関数
// -------------------------

// 件数更新
function updateCounts() {
	const pendingCountEl = document.getElementById("pending-count");
	const pendingList = document.getElementById("pending-list");
	const completedList = document.getElementById("completed-list");

	const pending = pendingList ? pendingList.querySelectorAll(".todo-card").length : 0;
	const completed = completedList ? completedList.querySelectorAll(".todo-card").length : 0;

	if (pendingCountEl) pendingCountEl.textContent = pending;
	// 完了件数は見た目でわかるが、必要なら DOM に追加して更新可能
}

// カード移動（未完了 ↔ 完了）
function moveCardToList(card, targetList, markDone) {
	if (!card || !targetList) return;
	card.classList.toggle("done", !!markDone); // 完了スタイル切替
	const cb = card.querySelector(".todo-checkbox");
	if (cb) cb.checked = !!markDone;          // チェック状態反映
	targetList.appendChild(card);             // DOM移動
	updateCounts();
}

// -------------------------
// チェックボックス切替処理
// -------------------------
async function handleToggle(id, card, checked) {
	try {
		// サーバー側の GET /todos/{id}/toggle を呼ぶ
		const resp = await fetch(`/todos/${id}/toggle`, { method: "GET" });
		if (!resp.ok) throw new Error("Network response was not ok");

		const pendingList = document.getElementById("pending-list");
		const completedList = document.getElementById("completed-list");

		if (checked) {
			moveCardToList(card, completedList, true);   // 完了に移動
		} else {
			moveCardToList(card, pendingList, false);   // 未完了に戻す
		}
	} catch (err) {
		console.error(err);
		alert("更新に失敗しました");
		const cb = card.querySelector(".todo-checkbox");
		if (cb) cb.checked = !checked; // ロールバック
	}
}

// -------------------------
// DOMロード時イベント
// -------------------------
document.addEventListener("DOMContentLoaded", function() {
	// チェックボックス変更時
	document.addEventListener("change", function(e) {
		if (e.target.matches(".todo-checkbox")) {
			const id = e.target.getAttribute("data-id");
			const card = e.target.closest(".todo-card");
			if (!card) return;
			handleToggle(id, card, e.target.checked);
		}
	});

	// 編集リンククリック（イベント委譲）
	document.addEventListener("click", function(e) {
		if (e.target.matches(".edit-link")) {
			e.preventDefault();
			const url = e.target.getAttribute("href");
			// 編集画面へ移動
			window.location.href = url;
		}
	});

	// 完了タスクの折りたたみ表示切替
	const toggleBtn = document.getElementById("toggle-completed-btn");
	if (toggleBtn) {
		toggleBtn.addEventListener("click", function() {
			const completedSection = document.getElementById("completed-list");
			if (!completedSection) return;
			completedSection.classList.toggle("hidden");
			toggleBtn.textContent = completedSection.classList.contains("hidden") ? "表示する" : "非表示にする";
		});
	}

	// 初期件数更新
	updateCounts();
});
