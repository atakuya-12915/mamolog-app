// =========================
// app.js
// =========================

document.addEventListener("DOMContentLoaded", function() {
	// -------------------------
	// ハンバーガーメニュー
	// -------------------------
	const menuToggle = document.querySelector("#menu-toggle");
	const sideMenu = document.querySelector("#side-menu");
	const overlay = document.createElement("div");
	overlay.id = "overlay";
	document.body.appendChild(overlay);

	if(menuToggle) {
		menuToggle.addEventListener("click", () => {
			sideMenu?.classList.toggle("active");
			overlay.classList.toggle("active");
		});
	}

	document.querySelectorAll(".nav-link").forEach(link => {
		link.addEventListener("click", e => {
			const href = link.getAttribute("href");
			if (href.startsWith("#")) e.preventDefault();
			sideMenu?.classList.remove("active");
			overlay.classList.remove("active");
		});
	});

	overlay.addEventListener("click", () => {
		sideMenu?.classList.remove("active");
		overlay.classList.remove("active");
	});

	// -------------------------
	// タグボタン選択（Todo作成画面）
	// -------------------------
	const tagBtns = document.querySelectorAll('.tag-btn');
	const tagInput = document.getElementById('tagInput');

	tagBtns.forEach(btn => {
		if (btn.dataset.tag === tagInput?.value) btn.classList.add('selected');

		btn.addEventListener('click', () => {
			tagBtns.forEach(b => b.classList.remove('selected'));
			btn.classList.add('selected');
			if(tagInput) tagInput.value = btn.dataset.tag;
		});
	});

	// -------------------------
	// 件数更新
	// -------------------------
	function updateCounts() {
		const pendingCountEl = document.getElementById("pending-count");
		const pendingList = document.getElementById("pending-list");
		const completedList = document.getElementById("completed-list");

		const pending = pendingList ? pendingList.querySelectorAll(".todo-card").length : 0;
		const completed = completedList ? completedList.querySelectorAll(".todo-card").length : 0;

		if (pendingCountEl) pendingCountEl.textContent = pending;
	}

	// -------------------------
	// カード移動
	// -------------------------
	function moveCardToList(card, targetList, markDone) {
		if (!card || !targetList) return;
		card.classList.toggle("done", !!markDone);
		const cb = card.querySelector(".todo-checkbox");
		if (cb) cb.checked = !!markDone;
		targetList.appendChild(card);
		updateCounts();
	}

	// -------------------------
	// チェックボックス切替
	// -------------------------
	async function handleToggle(id, card, checked) {
		try {
			const resp = await fetch(`/todos/${id}/toggle`, { method: "GET" });
			if (!resp.ok) throw new Error("Network response was not ok");

			const pendingList = document.getElementById("pending-list");
			const completedList = document.getElementById("completed-list");

			if (checked) moveCardToList(card, completedList, true);
			else moveCardToList(card, pendingList, false);
		} catch (err) {
			console.error(err);
			alert("更新に失敗しました");
			const cb = card.querySelector(".todo-checkbox");
			if(cb) cb.checked = !checked;
		}
	}

	// -------------------------
	// DOM操作（イベント委譲）
	// -------------------------
	document.addEventListener("change", function(e) {
		if(e.target.matches(".todo-checkbox")) {
			const id = e.target.getAttribute("data-id");
			const card = e.target.closest(".todo-card");
			if(!card) return;
			handleToggle(id, card, e.target.checked);
		}
	});

	document.addEventListener("click", function(e) {
		if(e.target.matches(".edit-link")) {
			e.preventDefault();
			const url = e.target.getAttribute("href");
			window.location.href = url;
		}
	});

	// -------------------------
	// 完了タスク折りたたみ
	// -------------------------
	const toggleBtn = document.getElementById("toggle-completed-btn");
	if(toggleBtn) {
		toggleBtn.addEventListener("click", function() {
			const completedSection = document.getElementById("completed-list");
			if(!completedSection) return;
			completedSection.classList.toggle("hidden");
			toggleBtn.textContent = completedSection.classList.contains("hidden") ? "表示する" : "非表示にする";
		});
	}

	// -------------------------
	// タブ切替（未完了/完了）
	// -------------------------
	document.addEventListener("DOMContentLoaded", function() {
    // タブ切替（未完了 / 完了）
    const tabs = document.querySelectorAll('.tab');
    const contents = document.querySelectorAll('.tab-content');

    tabs.forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();  // ページ遷移を防ぐ

            const target = this.dataset.tab;

            // タブの active クラス切替
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            // コンテンツ表示切替
            contents.forEach(c => {
                if(c.id === target) c.classList.remove('hidden');
                else c.classList.add('hidden');
            });
        });
    });

    // ページ内リンクや Home などは既存のイベント委譲に任せる
});

	// 初期件数更新
	updateCounts();
});
