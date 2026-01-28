package com.example.Controller.User;

import com.example.Common.Result;
import com.example.Service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
public class HeritageController {
    @Autowired
    private SiteService siteService;

    /**
     * 获取附近 HeritageSite
     * @param lng
     * @param lat
     * @return
     */
    @GetMapping("/initial")
    public ResponseEntity<?> getNearby(@RequestParam Double lng, @RequestParam Double lat) {
        return ResponseEntity.ok(Result.success(siteService.getNearbyHeritageSites(lng, lat)));
    }

}