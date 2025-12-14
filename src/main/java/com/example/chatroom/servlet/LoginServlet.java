package com.example.chatroom.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect("index.html?error=empty");
            return;
        }
        username = username.trim();
        ServletContext context = getServletContext();
        String[] onlineUsers = (String[]) context.getAttribute("onlineUsers");

        if (onlineUsers != null && onlineUsers.length > 0) {
            for (String user : onlineUsers) {
                if (user != null && user.equals(username)) {
                    response.sendRedirect("index.html?error=exists");
                    return;
                }
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute("username", username);
        addOnlineUser(context, username);
        response.sendRedirect("chat.html");
    }

    private void addOnlineUser(ServletContext context, String username) {
        String[] oldUsers = (String[]) context.getAttribute("onlineUsers");
        String[] newUsers;
        if (oldUsers == null) {
            newUsers = new String[]{username};
        } else {
            newUsers = new String[oldUsers.length + 1];
            for (int i = 0; i < oldUsers.length; i++) {
                newUsers[i] = oldUsers[i];
            }
            newUsers[oldUsers.length] = username;
        }
        context.setAttribute("onlineUsers", newUsers);
    }
}