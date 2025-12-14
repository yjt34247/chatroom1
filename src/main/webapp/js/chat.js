document.getElementById('welcomeMsg').innerHTML = '欢迎: 用户 | <a href="logout">退出</a>';

setInterval(() => {
    fetch('getMessages')
        .then(r => r.text())
        .then(html => document.getElementById('messages').innerHTML = html);
}, 1000);