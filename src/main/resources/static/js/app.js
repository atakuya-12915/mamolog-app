// ================================================
// app.js - まもログ 共通JavaScript
// ================================================

document.addEventListener('DOMContentLoaded', function() {
    
    // ================================================
    // サイドメニュー開閉 (修正④)
    // ================================================
    const menuToggle = document.getElementById('menu-toggle');
    const sideMenu = document.getElementById('side-menu');
    const menuOverlay = document.getElementById('menu-overlay');
    const sideMenuClose = document.querySelector('.side-menu-close');
    
    // ハンバーガーボタンクリック
    if (menuToggle) {
        menuToggle.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleMenu();
        });
    }
    
    // オーバーレイクリック
    if (menuOverlay) {
        menuOverlay.addEventListener('click', function() {
            closeMenu();
        });
    }
    
    // 閉じるボタンクリック
    if (sideMenuClose) {
        sideMenuClose.addEventListener('click', function() {
            closeMenu();
        });
    }
    
    // メニューの開閉
    function toggleMenu() {
        if (sideMenu && menuOverlay) {
            const isActive = sideMenu.classList.contains('active');
            
            if (isActive) {
                closeMenu();
            } else {
                openMenu();
            }
        }
    }
    
    // メニューを開く
    function openMenu() {
        if (sideMenu && menuOverlay) {
            sideMenu.classList.add('active');
            menuOverlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    }
    
    // メニューを閉じる
    function closeMenu() {
        if (sideMenu && menuOverlay) {
            sideMenu.classList.remove('active');
            menuOverlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    }
    
    // グローバルに公開（fragment.htmlから呼べるように）
    window.toggleMenu = toggleMenu;
    
    
    // ================================================
    // Todoチェックボックスのトグル処理
    // ================================================
    const checkboxes = document.querySelectorAll('.todo-checkbox');
    
    checkboxes.forEach(function(checkbox) {
        checkbox.addEventListener('change', function() {
            const todoId = this.getAttribute('data-id');
            
            fetch('/todos/' + todoId + '/toggle', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                if (response.ok) {
                    // 成功したらページをリロード
                    location.reload();
                } else {
                    console.error('Failed to toggle todo');
                    // エラー時はチェックを元に戻す
                    this.checked = !this.checked;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                // エラー時はチェックを元に戻す
                this.checked = !this.checked;
            });
        });
    });
    
    
    // ================================================
    // 完了タスクの折りたたみ
    // ================================================
    const toggleBtn = document.getElementById('toggle-completed-btn');
    const completedList = document.getElementById('completed-list');
    
    if (toggleBtn && completedList) {
        toggleBtn.addEventListener('click', function() {
            if (completedList.style.display === 'none') {
                completedList.style.display = 'flex';
                this.textContent = '非表示にする';
            } else {
                completedList.style.display = 'none';
                this.textContent = '表示する';
            }
        });
    }
    
    
    // ================================================
    // 検索キーワードのハイライト (オプション)
    // ================================================
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const keyword = this.value.toLowerCase();
            const todoCards = document.querySelectorAll('.todo-card');
            
            todoCards.forEach(function(card) {
                const title = card.querySelector('.todo-title');
                const memo = card.querySelector('.todo-meta');
                
                if (title) {
                    const titleText = title.textContent.toLowerCase();
                    const memoText = memo ? memo.textContent.toLowerCase() : '';
                    
                    if (titleText.includes(keyword) || memoText.includes(keyword)) {
                        card.style.display = '';
                    } else {
                        card.style.display = keyword ? 'none' : '';
                    }
                }
            });
        });
    }
    
    
    // ================================================
    // フラッシュメッセージの自動非表示
    // ================================================
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(function() {
                alert.remove();
            }, 500);
        }, 5000); // 5秒後に自動で消える
    });
    
});

// ================================================
// ページ読み込み時のスムーズスクロール
// ================================================
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});