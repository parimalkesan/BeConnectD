package model;

public class Message {
    private String senderId;
    private String receiverId;
    private String message;

    public Message(String message,String receiverId, String senderId) {
        this.message = message;
        this.receiverId = receiverId;
        this.senderId = senderId;
    }
    public Message()
    {}


    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }
}


