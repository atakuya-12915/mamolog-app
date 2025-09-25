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
const menuToggle = document.querySelector("#menu-toggle"); // メニュー開閉ボタン
const sideMenu = document.querySelector("#side-menu");     // サイドメニュー
const overlay = document.createElement("div");             // 背景オーバーレイ
overlay.id = "overlay";
document.body.appendChild(overlay);                        // DOMに追加

// メニュー開閉ボタン押下時
menuToggle.addEventListener("click", () => {
	sideMenu.classList.toggle("active");   // メニュー表示/非表示
	overlay.classList.toggle("active");    // 背景オーバーレイ表示/非表示
});

// メニュー内リンククリックで閉じる
document.querySelectorAll(".nav-link").forEach(link => {
	link.addEventListener("click", e => {
		const href = link.getAttribute("href");
		if (href.startsWith("#")) e.preventDefault(); // #リンクはページ遷移させない
		sideMenu.classList.remove("active");          // メニュー閉じる
		overlay.classList.remove("active");           // オーバーレイ閉じる
	});
});

// overlayクリックで閉じる
overlay.addEventListener("click", () => {
	sideMenu.classList.remove("active");
	overlay.classList.remove("active");
});

// -------------------------
// タグボタン選択（例：Todo作成画面）
// -------------------------
const tagBtns = document.querySelectorAll('.tag-btn'); // タグボタン群
const tagInput = document.getElementById('tagInput');  // hidden input

tagBtns.forEach(btn => {
	// 初期値と一致するタグを選択状態にする
	if (btn.dataset.tag === tagInput?.value) {
		btn.classList.add('selected');
	}

	// タグボタン押下時
	btn.addEventListener('click', () => {
		// すべてのボタンの選択を解除
		tagBtns.forEach(b => b.classList.remove('selected'));
		btn.classList.add('selected');          // クリックしたボタンを選択
		if(tagInput) tagInput.value = btn.dataset.tag; // hidden input に値を設定
	});
});

// -------------------------
// ヘルパー関数
// -------------------------

// 件数更新
function updateCounts() {
	const pendingCountEl = document.getElementById("pending-count"); // 未完了件数表示
	const pendingList = document.getElementById("pending-list");     // 未完了リスト
	const completedList = document.getElementById("completed-list"); // 完了リスト

	const pending = pendingList ? pendingList.querySelectorAll(".todo-card").length : 0;
	const completed = completedList ? completedList.querySelectorAll(".todo-card").length : 0;

	if (pendingCountEl) pendingCountEl.textContent = pending; // 未完了件数更新
	// 完了件数は必要に応じてDOMに表示可能
}

// カード移動（未完了 ↔ 完了）
function moveCardToList(card, targetList, markDone) {
	if (!card || !targetList) return;

	// 完了クラス切替
	card.classList.toggle("done", !!markDone);

	// チェックボックス状態を反映
	const cb = card.querySelector(".todo-checkbox");
	if (cb) cb.checked = !!markDone;

	// リスト間でDOMを移動
	targetList.appendChild(card);

	// 件数更新
	updateCounts();
}

// -------------------------
// チェックボックス切替処理
// -------------------------
async function handleToggle(id, card, checked) {
	try {
		// サーバー側に切替を通知
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
	// -------------------------
	// チェックボックス変更時
	// -------------------------
	document.addEventListener("change", function(e) {
		if (e.target.matches(".todo-checkbox")) {
			const id = e.target.getAttribute("data-id");
			const card = e.target.closest(".todo-card");
			if (!card) return;
			handleToggle(id, card, e.target.checked);
		}
	});

	// -------------------------
	// 編集リンククリック（イベント委譲）
	// -------------------------
	document.addEventListener("click", function(e) {
		if (e.target.matches(".edit-link")) {
			e.preventDefault(); // デフォルトリンク動作停止
			const url = e.target.getAttribute("href");
			window.location.href = url; // 編集画面へ移動
		}
	});

	// -------------------------
	// 完了タスクの折りたたみ表示切替
	// -------------------------
	const toggleBtn = document.getElementById("toggle-completed-btn");
	if (toggleBtn) {
		toggleBtn.addEventListener("click", function() {
			const completedSection = document.getElementById("completed-list");
			if (!completedSection) return;
			completedSection.classList.toggle("hidden"); // hiddenクラスで表示/非表示
			toggleBtn.textContent = completedSection.classList.contains("hidden") ? "表示する" : "非表示にする";
		});
	}

	// -------------------------
	// 初期件数更新
	// -------------------------
	updateCounts();
});
