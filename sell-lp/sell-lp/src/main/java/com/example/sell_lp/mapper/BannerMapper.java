package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.banner.BannerRequest;
import com.example.sell_lp.dto.response.banner.BannerResponse;
import com.example.sell_lp.entity.Banner;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BannerMapper {

    Banner toBanner(BannerRequest bannerRequest);


    BannerResponse toBannerResponse(Banner banner);

}
