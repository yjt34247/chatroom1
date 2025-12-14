document.addEventListener('DOMContentLoaded', function() {
    if (window.location.href.indexOf('error=empty') > -1) {
        showError('用户名不能为空');
    }
    if (window.location.href.indexOf('error=exists') > -1) {
        showError('用户名已存在');
    }

    const loginForm = document.querySelector('#loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const username = document.querySelector('#username').value.trim();
            if (username) {
                const tabId = Date.now() + Math.random().toString(36).substr(2, 9);
                sessionStorage.setItem('currentTabId', tabId);
                sessionStorage.setItem('username_' + tabId, username);
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = 'login';
                form.innerHTML = `
                    <input type="hidden" name="username" value="${username}">
                    <input type="hidden" name="tabId" value="${tabId}">
                `;
                document.body.appendChild(form);
                form.submit();
            } else {
                showError('用户名不能为空');
            }
        });
    }
});

function showError(message) {
    const errorMsg = document.querySelector('#errorMsg');
    if (errorMsg) {
        errorMsg.textContent = message;
        errorMsg.style.color = 'red';
    }
}