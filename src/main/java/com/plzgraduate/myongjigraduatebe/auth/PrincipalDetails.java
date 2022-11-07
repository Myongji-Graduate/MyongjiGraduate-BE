package com.plzgraduate.myongjigraduatebe.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails {

  private final AuthenticatedUser user;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    Collection<GrantedAuthority> authorities = new ArrayList<>();
    user
        .getRole()
        .getRoleList()
        .forEach(role -> authorities.add(() -> role));

    return authorities;
  }

  @Override
  public String getUsername() {
    return user.getName();
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public AuthenticatedUser getUser() {
    return user;
  }
}
