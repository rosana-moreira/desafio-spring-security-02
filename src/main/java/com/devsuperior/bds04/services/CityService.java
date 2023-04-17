package com.devsuperior.bds04.services;

import com.devsuperior.bds04.dto.*;
import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.repositories.CityRepository;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    public List<CityDTO> findAll() {
        List<City> list = cityRepository.findAll(Sort.by("name"));
        return list.stream().map(x -> new CityDTO(x)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CityDTO findById(Long id) {
        Optional<City> obj = cityRepository.findById(id);
        City entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new CityDTO(entity);

    }

    @Transactional
    public CityDTO insert(CityDTO dto) {
        City entity = new City();
        copyDtoEntity(dto, entity);
        entity = cityRepository.save(entity);
        return new CityDTO(entity);
    }

    @Transactional
    public CityDTO update(CityDTO dto, Long id) {
        try {
            City entity = cityRepository.getOne(id);
            copyDtoEntity(dto, entity);
            entity = cityRepository.save(entity);
            return new CityDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found" + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            cityRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found" + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");

        }
    }

    private void copyDtoEntity(CityDTO dto, City entity) {
        entity.setName(dto.getName());
    }
}
