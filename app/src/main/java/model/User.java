package model;

public class User {
    private String Email;
    private String ImageUrl;
    private String Userid;

    public User(String email,String imageUrl,String userId)
    {
        this.Userid=userId;
        this.Email=email;
        this.ImageUrl=imageUrl;
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
