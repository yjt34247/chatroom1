package com.example.chatroom.servlet;

import com.example.chatroom.model.Message;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class SendMessageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("index.html");
            return;
        }
        String username = (String) session.getAttribute("username");
        String messageContent = request.getParameter("message");

        if (messageContent != null && !messageContent.trim().isEmpty()) {
            addMessage(getServletContext(), username, messageContent.trim());
        }

        response.sendRedirect("chat.html");
    }

    private void addMessage(ServletContext context, String username, String content) {
        Message[] oldMessages = (Message[]) context.getAttribute("messages");
        Message[] newMessages;
        Message newMessage = new Message(username, content);

        if (oldMessages == null) {
            newMessages = new Message[1];
            newMessages[0] = newMessage;
        } else {
            newMessages = new Message[oldMessages.length + 1];
            for (int i = 0; i < oldMessages.length; i++) {
                newMessages[i] = oldMessages[i];
            }
            newMessages[oldMessages.length] = newMessage;
        }
        context.setAttribute("messages", newMessages);
    }
}