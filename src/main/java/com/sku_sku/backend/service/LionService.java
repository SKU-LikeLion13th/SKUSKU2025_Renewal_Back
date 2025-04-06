package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.dto.Response.LionDTO;
import com.sku_sku.backend.enums.RoleType;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.exception.InvalidEmailException;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.exception.InvalidLionException;
import com.sku_sku.backend.repository.LionRepository;
import com.sku_sku.backend.security.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LionService {
    private final LionRepository lionRepository;
    private final JwtUtility jwtUtility;

    public String tokenToLionName(String token) {
        Lion lion = lionRepository.findByEmail(jwtUtility.getClaimsFromJwt(token).getSubject())
                .orElseThrow(InvalidLionException::new);
        return lion.getName();
    }

    public Lion tokenToLion(String token) {
        return lionRepository.findByEmail(jwtUtility.getClaimsFromJwt(token).getSubject())
                .orElseThrow(InvalidLionException::new);
    }

    @Transactional
    public Lion addLion(String name, String email, TrackType track, RoleType role) {
        if (lionRepository.findByEmail(email).isPresent()) {
            throw new InvalidEmailException();
        }
        Lion lion = new Lion(name, email, track, role);
        return lionRepository.save(lion);
    }

    @Transactional
    public Lion updateLion(Long id, String name, String email, TrackType track, RoleType role) {
        Lion lion = lionRepository.findById(id)
                .orElseThrow(() -> new InvalidIdException("lion"));
        String newName = (name != null && !name.isEmpty() ? name : lion.getName());
        String newEmail = (email != null && !email.isEmpty() ? email : lion.getEmail());
        TrackType newTrack = (track != null ? track : lion.getTrackType());
        RoleType newRole = (role != null ? role : lion.getRoleType());

        lion.update(newName, newEmail, newTrack, newRole);
        return lionRepository.save(lion);
    }

    @Transactional
    public void deleteLion(Long id) {
        Lion lion = lionRepository.findById(id)
                .orElseThrow(() -> new InvalidIdException("lion"));
        lionRepository.delete(lion);
    }

    public List<Lion> getAllLions() {
        return lionRepository.findAll();
    }

    public List<String> findWritersByTrack(TrackType track) {
        List<Lion> lions = lionRepository.findWritersByTrackType(track);
        return lions.stream()
                .map(Lion::getName)
                .toList();
    }

    public List<String> findWritersByTrackAndBaby(TrackType track) {
        List<Lion> lions = lionRepository.findWritersByTrackTypeAndRoleType(track, RoleType.BABY_LION);
        return lions.stream()
                .map(Lion::getName)
                .sorted()
                .toList();
    }


    public LionDTO.ResponseLionUpdate findLionById(Long id) {
        return lionRepository.findById(id)
                .map(lion -> new LionDTO.ResponseLionUpdate(
                        lion.getName(),
                        lion.getEmail(),
                        lion.getTrackType(),
                        lion.getRoleType()))
                .orElse(null);
    }
}
