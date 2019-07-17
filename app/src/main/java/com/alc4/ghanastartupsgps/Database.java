package com.alc4.ghanastartupsgps;

class Database {
    private String username;
    private String phone;
    private String _id;

    public Database() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Database(String username, String phone, String _id) {
        this.username = username;
        this.phone = phone;
        this._id = _id;
    }
}
