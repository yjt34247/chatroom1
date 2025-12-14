package com.example.chatroom.servlet;

import com.example.chatroom.model.Message;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class GetMessagesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("index.html");
            return;
        }
        String username = (String) session.getAttribute("username");
        Message[] messages = (Message[]) getServletContext().getAttribute("messages");
        if (messages != null) {
            for (Message msg : messages) {
                if (msg != null) {
                    response.getWriter().println("<b>" + msg.getUsername() + ":</b> " + msg.getContent());
                }
            }
        }
    }
}