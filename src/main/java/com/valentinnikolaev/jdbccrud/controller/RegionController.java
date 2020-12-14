package com.valentinnikolaev.jdbccrud.controller;

import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope ("singleton")
public class RegionController {

    private RegionRepository regionRepository;

    public RegionController(@Autowired RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public void addRegion(String name) {
        Optional<Region> regionOptional = regionRepository.add(
                new Region(getLastRegionId() + 1, name));

        if (regionOptional.isPresent()) {
            System.out.printf("Region with name %1$s added into database.", name);
        } else {
            System.out.printf("Region with name %1$s is not added into database.", name);
        }
    }

    public Optional<Region> getRegionById(String regionId) {
        long id = Long.parseLong(regionId);
        Optional<Region> region = this.regionRepository.isContains(id)
                                  ? this.regionRepository.get(id)
                                  : Optional.empty();

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

        Optional<Region> requestedRegion = indexOfRequestedRegion != - 1
                                           ? Optional.of(regionsList.get(indexOfRequestedRegion))
                                           : Optional.empty();
        return requestedRegion;
    }

    public boolean changeRegionName(String regionId, String newRegionName) {
        long id = Long.parseLong(regionId);
        Optional<Region> regionOptional = regionRepository.get(id);

        Optional<Region> regionFromDbOptional = Optional.empty();
        if (regionOptional.isPresent()) {
            Region region = regionOptional.get();
            region.setName(newRegionName);
            regionFromDbOptional = regionRepository.change(region);
        } else {
            System.out.printf("\nRegion with id %1$d not exists in database \n", id);
        }

        if (regionFromDbOptional.isPresent() &&
            regionOptional.get().equals(regionFromDbOptional.get())) {
            System.out.println("Region was changed.");
            return true;
        } else {
            System.out.println("Region was not changed.");
            return false;
        }
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
        Optional<Long> lastRegionId = this.regionRepository
                .getAll()
                .stream()
                .map(Region::getId)
                .max(Long::compareTo);
        return lastRegionId.isPresent()
               ? lastRegionId.get()
               : 0;
    }
}
