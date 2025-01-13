package com.core.arnuv.services.imp;

import com.core.arnuv.model.MenuItem;
import com.core.arnuv.repository.IMenuItemRepository;
import com.core.arnuv.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuService implements IMenuService {
    private final IMenuItemRepository menuItemRepository;
    @Override
    public Set<MenuItem> getMenuByRoles(Set<String> roles) {
        return menuItemRepository.findAllByRoles(roles);
    }
}
