package org.com.application.Model;

public class PostModel {
    String id,category_id,title,body,created_at,image;

    public PostModel(String id, String category_id, String title, String body, String created_at, String image) {
        this.id = id;
        this.category_id = category_id;
        this.title = title;
        this.body = body;
        this.created_at = created_at;
        this.image = image;
    }

    public PostModel(String id, String category_id, String title, String created_at, String image) {
        this.id = id;
        this.category_id = category_id;
        this.title = title;
        this.created_at = created_at;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
