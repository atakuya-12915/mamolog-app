document.addEventListener("DOMContentLoaded", () => {
	// 仮データ
	const sampleTask = {
		id: 1,
		title: "買い物に行く",
		assignee: "ぱぱ",
		tag: "家事",
		deadline: "2025-09-20",
		time: "10:00",
		color: "#ffcc00",
		memo: "牛乳を忘れずに"
	};

	// 初期表示
	document.getElementById("title").value = sampleTask.title;
	document.getElementById("assignee").value = sampleTask.assignee === "ぱぱ" ? "papa" : "mama";
	document.getElementById("deadline").value = sampleTask.deadline;
	document.getElementById("time").value = sampleTask.time;
	document.getElementById("color").value = sampleTask.color;
	document.getElementById("memo").value = sampleTask.memo;

	// 更新処理（仮）
	document.getElementById("editTaskForm").addEventListener("submit", (e) => {
		e.preventDefault();
		alert("タスクを更新しました！（仮）");
		window.location.href = "index.html";
	});

	// 削除処理（仮）
	document.getElementById("deleteBtn").addEventListener("click", () => {
		if (confirm("本当に削除しますか？")) {
			alert("タスクを削除しました！（仮）");
			window.location.href = "index.html";
		}
	});
});
