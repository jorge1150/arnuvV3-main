package com.core.arnuv.service;

import com.core.arnuv.model.MenuItem;

import java.util.Set;

public interface IMenuService {
    Set<MenuItem> getMenuByRoles(Set<String> roles);
}
