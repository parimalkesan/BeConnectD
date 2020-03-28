package model;
//User class to define attributes of a user
public class User {
    private String Email;
    private String ImageUrl;
    private String Userid;
    private String Status;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public User(String email, String imageUrl, String Status,String userId)
    {
        this.Userid=userId;
        this.Email=email;
        this.ImageUrl=imageUrl;
        this.Status=Status;
        this.Userid=userId;
    }

    public User()
    {

    }

    public String getUserId() {
        return Userid;
    }

    public String getEmail() {
        return Email;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setUserId(String userId) {
        this.Userid = userId;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }
}
