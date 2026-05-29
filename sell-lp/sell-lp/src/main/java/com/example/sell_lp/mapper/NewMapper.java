package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.news.NewRequest;
import com.example.sell_lp.dto.response.news.NewResponse;
import com.example.sell_lp.entity.New;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface NewMapper {

    New toNew(NewRequest newRequest);

    NewResponse toNewResponse(New newRequest);
}
