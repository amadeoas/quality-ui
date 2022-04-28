package co.uk.bocaditos.ui.views.home;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


public class Person {

	@Size(min=5)
    private String image;

	@Pattern(regexp="^[a-zA-Z]{2,20}$")
	@Size(min=2, max=20)
	private String name;

	@Pattern(regexp="^[a-zA-Z]{10,10}$")
	@Size(min=10, max=10)
	private String date;

	@Pattern(regexp="^[A-Z\\s]{8,9}$")
	@Size(min=8, max=9)
	private String post;

	private String likes;
    private String comments;
    private String shares;


    public Person() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

}
