package indi.sword.shiro.realms;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;

public class ShiroRealm implements Realm{
    private HashedCredentialsMatcher credentialsMatcher;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return false;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return null;
    }

    public void setCredentialsMatcher(HashedCredentialsMatcher credentialsMatcher) {
        this.credentialsMatcher = credentialsMatcher;
    }

    public HashedCredentialsMatcher getCredentialsMatcher() {
        return credentialsMatcher;
    }
}
