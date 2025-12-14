package com.example.chatroom.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            if (username != null) {
                removeOnlineUser(getServletContext(), username);
            }
            session.invalidate();
        }

        response.setContentType("text/html;charset=UTF-8");
        java.io.PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>已退出聊天室</h2>");
        out.println("<a href='index.html'>重新登录</a>");
        out.println("</body></html>");
    }

    private void removeOnlineUser(ServletContext context, String username) {
        String[] oldUsers = (String[]) context.getAttribute("onlineUsers");
        if (oldUsers == null) return;

        if (oldUsers.length == 1 && oldUsers[0].equals(username)) {
            context.removeAttribute("onlineUsers");
            return;
        }

        int removeIndex = -1;
        for (int i = 0; i < oldUsers.length; i++) {
            if (oldUsers[i] != null && oldUsers[i].equals(username)) {
                removeIndex = i;
                break;
            }
        }

        if (removeIndex != -1) {
            String[] newUsers = new String[oldUsers.length - 1];
            int newIndex = 0;
            for (int i = 0; i < oldUsers.length; i++) {
                if (i != removeIndex) {
                    newUsers[newIndex] = oldUsers[i];
                    newIndex++;
                }
            }
            context.setAttribute("onlineUsers", newUsers);
        }
    }
}