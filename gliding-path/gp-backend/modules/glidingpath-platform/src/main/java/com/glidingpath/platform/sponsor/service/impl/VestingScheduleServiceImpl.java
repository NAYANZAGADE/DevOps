package com.glidingpath.platform.sponsor.service.impl;

import com.glidingpath.platform.shared.dto.MasterVestingScheduleDTO;
import com.glidingpath.platform.shared.dto.TenantVestingScheduleDTO;
import com.glidingpath.platform.shared.dto.VestingScheduleDetailDTO;
import com.glidingpath.platform.sponsor.service.VestingScheduleService;
import com.glidingpath.core.entity.MasterVestingSchedule;
import com.glidingpath.core.entity.TenantVestingSchedule;
import com.glidingpath.core.entity.VestingScheduleDetail;
import com.glidingpath.core.repository.MasterVestingScheduleRepository;
import com.glidingpath.core.repository.TenantVestingScheduleRepository;
import com.glidingpath.core.repository.VestingScheduleDetailRepository;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VestingScheduleServiceImpl implements VestingScheduleService {

    @Autowired
    private MasterVestingScheduleRepository masterVestingScheduleRepository;

    @Autowired
    private TenantVestingScheduleRepository tenantVestingScheduleRepository;

    @Autowired
    private VestingScheduleDetailRepository vestingScheduleDetailRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Master Vesting Schedule operations
    @Override
    public MasterVestingScheduleDTO getMasterVestingScheduleById(UUID id) {
        MasterVestingSchedule entity = masterVestingScheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Master vesting schedule not found with ID: " + id));
        return modelMapper.map(entity, MasterVestingScheduleDTO.class);
    }

    @Override
    public List<MasterVestingScheduleDTO> getAllMasterVestingSchedules() {
        List<MasterVestingSchedule> entities = masterVestingScheduleRepository.findAll();
        return entities.stream()
                .map(entity -> modelMapper.map(entity, MasterVestingScheduleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public MasterVestingScheduleDTO createMasterVestingSchedule(MasterVestingScheduleDTO dto) {
        MasterVestingSchedule entity = modelMapper.map(dto, MasterVestingSchedule.class);
        entity = masterVestingScheduleRepository.save(entity);
        return modelMapper.map(entity, MasterVestingScheduleDTO.class);
    }

    // Tenant Vesting Schedule operations
    @Override
    public TenantVestingScheduleDTO getTenantVestingScheduleById(UUID id) {
        TenantVestingSchedule entity = tenantVestingScheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Tenant vesting schedule not found with ID: " + id));
        return modelMapper.map(entity, TenantVestingScheduleDTO.class);
    }

    @Override
    public List<TenantVestingScheduleDTO> getAllTenantVestingSchedules() {
        List<TenantVestingSchedule> entities = tenantVestingScheduleRepository.findAll();
        return entities.stream()
                .map(entity -> modelMapper.map(entity, TenantVestingScheduleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TenantVestingScheduleDTO createTenantVestingSchedule(TenantVestingScheduleDTO dto) {
        TenantVestingSchedule entity = modelMapper.map(dto, TenantVestingSchedule.class);
        entity = tenantVestingScheduleRepository.save(entity);
        return modelMapper.map(entity, TenantVestingScheduleDTO.class);
    }

    // Vesting Schedule Details operations
    @Override
    public List<VestingScheduleDetailDTO> getVestingScheduleDetailsByMasterId(UUID masterId) {
        List<VestingScheduleDetail> entities = vestingScheduleDetailRepository.findByVestingScheduleId(masterId);
        return entities.stream()
                .map(entity -> modelMapper.map(entity, VestingScheduleDetailDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VestingScheduleDetailDTO> getVestingScheduleDetailsByTenantId(UUID tenantId) {
        List<VestingScheduleDetail> entities = vestingScheduleDetailRepository.findByTenantVestingScheduleId(tenantId);
        return entities.stream()
                .map(entity -> modelMapper.map(entity, VestingScheduleDetailDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VestingScheduleDetailDTO createVestingScheduleDetail(VestingScheduleDetailDTO dto) {
        VestingScheduleDetail entity = modelMapper.map(dto, VestingScheduleDetail.class);
        entity = vestingScheduleDetailRepository.save(entity);
        return modelMapper.map(entity, VestingScheduleDetailDTO.class);
    }
} 