package com.wishbook.catalog.commonmodels.responses;

public class Profile
{
    private String id;

    private String first_name;

    private String username;

    private String date_joined;

    private String is_active;

    private Companyuser companyuser;

    private String email;

    private String last_name;

    private String password;

    private Userprofile userprofile;

    private String[] groups;

    private String last_login;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getFirst_name ()
    {
        return first_name;
    }

    public void setFirst_name (String first_name)
    {
        this.first_name = first_name;
    }

    public String getUsername ()
    {
        return username;
    }

    public void setUsername (String username)
    {
        this.username = username;
    }

    public String getDate_joined ()
    {
        return date_joined;
    }

    public void setDate_joined (String date_joined)
    {
        this.date_joined = date_joined;
    }

    public String getIs_active ()
    {
        return is_active;
    }

    public void setIs_active (String is_active)
    {
        this.is_active = is_active;
    }

    public Companyuser getCompanyuser ()
    {
        return companyuser;
    }

    public void setCompanyuser (Companyuser companyuser)
    {
        this.companyuser = companyuser;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getLast_name ()
    {
        return last_name;
    }

    public void setLast_name (String last_name)
    {
        this.last_name = last_name;
    }

    public String getPassword ()
    {
        return password;
    }

    public void setPassword (String password)
    {
        this.password = password;
    }

    public Userprofile getUserprofile ()
    {
        return userprofile;
    }

    public void setUserprofile (Userprofile userprofile)
    {
        this.userprofile = userprofile;
    }

    public String[] getGroups ()
    {
        return groups;
    }

    public void setGroups (String[] groups)
    {
        this.groups = groups;
    }

    public String getLast_login ()
    {
        return last_login;
    }

    public void setLast_login (String last_login)
    {
        this.last_login = last_login;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", first_name = "+first_name+", username = "+username+", date_joined = "+date_joined+", is_active = "+is_active+", companyuser = "+companyuser+", email = "+email+", last_name = "+last_name+", password = "+password+", userprofile = "+userprofile+", groups = "+groups+", last_login = "+last_login+"]";
    }
}

	