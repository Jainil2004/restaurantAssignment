package com.slooze.SDE.DTO;

import com.slooze.SDE.model.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private Long id;
    private String name;
    private Country country;
    private List<MenuItemDto> menuItemList;
}
