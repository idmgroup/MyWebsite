package fr.idm.myWebsite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    SQLiteUserDetailsManager userDetailsManager;

    public boolean checkSuspicious(String username) {
        if (username != null && username.contains("t"))
            return true;
        return false;
    }

    public boolean checkExist(String username) {
        return userDetailsManager.userExists(username);
    }

    public void registerUser(fr.idm.myWebsite.User user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_NEW"));
        UserDetails ud = new User(user.getUsername(), user.getPassword(), authorities);
        userDetailsManager.createUser(ud);
    }

    public void setAdmin(String username) {
        UserDetails ud = userDetailsManager.loadUserByUsername(username);
        if (ud != null) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(ud.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

            User user = new User(ud.getUsername(), ud.getPassword(), authorities);
            userDetailsManager.updateUser(user);
        }
    }

    public UserDetails loadUserByUsername(String username) {
        return userDetailsManager.loadUserByUsername(username);
    }

    public boolean userExists(String username) {
        return userDetailsManager.userExists(username);
    }

    public Map<String, UserDetails> getUsers() {
        return userDetailsManager.getUsers();

    }

}
