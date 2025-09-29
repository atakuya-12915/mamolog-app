// 写真プレビュー
const photoInput = document.getElementById('photoInput');
const photoPreview = document.getElementById('photoPreview');

photoInput.addEventListener('change', function() {
	const file = this.files[0];
	if (file) {
		const reader = new FileReader();
		reader.onload = function(e) {
			photoPreview.src = e.target.result;
			photoPreview.style.display = 'block';
		}
		reader.readAsDataURL(file);
	} else {
		photoPreview.src = '#';
		photoPreview.style.display = 'none';
	}
});

// タグボタン選択
const tagButtons = document.querySelectorAll('.tag-btn');
tagButtons.forEach(btn => {
	btn.addEventListener('click', function() {
		btn.classList.toggle('active');
	});
});