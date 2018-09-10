package fr.idm.mywebsite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

public class SQLiteUserDetailsManager implements UserDetailsManager {

    Connection connection = null;

    public SQLiteUserDetailsManager(Properties users) throws SQLException {
        dbInit();
        Enumeration<?> names = users.propertyNames();
        UserAttributeEditor editor = new UserAttributeEditor();

        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            editor.setAsText(users.getProperty(name));
            UserAttribute attr = (UserAttribute) editor.getValue();
            UserDetails user = new User(name, attr.getPassword(), attr.isEnabled(), true, true, true,
                    attr.getAuthorities());
            createUser(user);
        }

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void createUser(UserDetails user) {
        Assert.isTrue(!userExists(user.getUsername()));
        try {
            String createUser = "insert into users values( ? , ? ,? )";
            PreparedStatement ps = connection.prepareStatement(createUser);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getAuthorities().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void dbInit() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("create table users(username, password, roles)");
    }

    @Override
    public void deleteUser(String username) {
        try {
            String deleteUser = "delete * from users where username= ?";
            PreparedStatement ps = connection.prepareStatement(deleteUser);
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public Map<String, UserDetails> getUsers() {
        Statement stmt;
        Map<String, UserDetails> users = new HashMap<String, UserDetails>();
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select username from users");
            while (rs.next()) {
                String username = rs.getString("username");
                users.put(username, loadUserByUsername(username));
            }
        } catch (SQLException e) {
        }
        return users;
    }

    @Override
    public UserDetails loadUserByUsername(String psUsername) {
        User user = null;
        String getUser = "select username, password, roles from users where username =  ?";
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(getUser);
            ps.setString(1, psUsername);
            ResultSet rs = ps.executeQuery();

            List<GrantedAuthority> authoritiesAsStrings = new ArrayList<GrantedAuthority>();
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String roles = rs.getString("roles");
                String[] splittedRoles = roles.substring(1, roles.length() - 1).split(", ");
                for (String role : splittedRoles) {
                    authoritiesAsStrings.add(new SimpleGrantedAuthority(role));
                }

                user = new User(username, password, authoritiesAsStrings);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (user == null) {
            throw new UsernameNotFoundException(psUsername);
        }

        return user;
    }

    @Override
    public void updateUser(UserDetails user) {
        Assert.isTrue(userExists(user.getUsername()));
        try {
            String updateUser = "update users set password = ? , roles = ? where username =  ?";
            PreparedStatement ps = connection.prepareStatement(updateUser);
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getAuthorities().toString());
            ps.setString(3, user.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean userExists(String username) {
        Statement stmt;
        try {
            stmt = connection.createStatement();
            String SQL = "select * from users where username=\"" + username + "\"";
            ResultSet rs = stmt.executeQuery(SQL);
            return rs.next();
        } catch (SQLException e) {
            // an error, then its not an existing user
        }
        return false;
    }

}
