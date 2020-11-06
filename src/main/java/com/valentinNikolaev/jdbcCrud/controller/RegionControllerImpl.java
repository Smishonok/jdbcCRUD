package com.valentinNikolaev.jdbcCrud.controller;

import com.valentinNikolaev.jdbcCrud.models.Region;
import com.valentinNikolaev.jdbcCrud.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

public class RegionControllerImpl implements RegionController {

    private RegionRepository regionRepository;

    public RegionControllerImpl(RegionRepository regionRepository)  {
        this.regionRepository = regionRepository;
    }

    public Region addRegion(String name) {
        Region region = regionRepository.add(new Region(getLastRegionId() + 1, name));
        return region;
    }

    public Optional<Region> getRegionById(String regionId) {
        long id = Long.parseLong(regionId);
        Optional<Region> region = this.regionRepository.isContains(id) ? Optional.of(
                this.regionRepository.get(id)) : Optional.empty();

        return region;
    }

    public Optional<Region> getRegionByName(String regionName) {
        List<Region> regionsList = this.regionRepository.getAll();

        int indexOfRequestedRegion = - 1;
        for (int i = 0; i < regionsList.size(); i++) {
            if (regionsList.get(i).getName().equals(regionName)) {
                indexOfRequestedRegion = i;
            }
        }

        Optional<Region> requestedRegion = indexOfRequestedRegion != - 1 ? Optional.of(
                regionsList.get(indexOfRequestedRegion)) : Optional.empty();
        return requestedRegion;
    }

    public boolean changeRegionName(String regionId, String newRegionName) {
        long id = Long.parseLong(regionId);
        if (this.regionRepository.isContains(id)) {
            Region region = this.regionRepository.get(id);
            region.setName(newRegionName);
            this.regionRepository.change(region);
        }
        return this.regionRepository.get(id).getName().equals(newRegionName);
    }

    public boolean removeRegionWithId(String regionId) {
        long id = Long.parseLong(regionId);
        boolean isRegionRemoved = regionRepository.remove(id);
        return isRegionRemoved;
    }

    public boolean removeAllRegions() {
        boolean isAllRegionsRemoved = regionRepository.removeAll();
        return isAllRegionsRemoved;
    }

    public List<Region> getAllRegions() {
        List<Region> regionList = regionRepository.getAll();
        return regionList;
    }

    private long getLastRegionId() {
        Optional<Long> lastRegionId = this.regionRepository.getAll().stream().map(Region::getId)
                                                           .max(Long::compareTo);
        return lastRegionId.isPresent() ? lastRegionId.get() : 0;
    }
}
