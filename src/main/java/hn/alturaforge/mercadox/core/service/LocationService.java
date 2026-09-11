package hn.alturaforge.mercadox.core.service;

import hn.alturaforge.mercadox.core.mapper.LocationMapper;
import hn.alturaforge.mercadox.library.entity.model.core.Location;
import hn.alturaforge.mercadox.library.entity.ports.incoming.LocationUseCase;
import hn.alturaforge.mercadox.library.entity.response.dto.LocationDto;
import hn.alturaforge.mercadox.library.jpa.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationService implements LocationUseCase {

    private LocationRepository locationRepository;

    private LocationMapper locationMapper;

    @Override
    public Location findById(String s) {
        return locationRepository.findById(UUID.fromString(s))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Location was not found for ID: '%s'", s)));
    }

    @Override
    public LocationDto saveLocation(Location location) {
        return locationMapper.toDto(locationRepository.save(location));
    }

    @Override
    public void deleteLocationById(String s) {
        locationRepository.deleteById(UUID.fromString(s));
    }
}
