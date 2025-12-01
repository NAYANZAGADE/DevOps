package com.glidingpath.platform.sponsor.service;

import com.glidingpath.platform.shared.dto.MasterVestingScheduleDTO;
import com.glidingpath.platform.shared.dto.TenantVestingScheduleDTO;
import com.glidingpath.platform.shared.dto.VestingScheduleDetailDTO;
import java.util.List;
import java.util.UUID;

public interface VestingScheduleService {
	MasterVestingScheduleDTO getMasterVestingScheduleById(UUID id);
	List<MasterVestingScheduleDTO> getAllMasterVestingSchedules();
	MasterVestingScheduleDTO createMasterVestingSchedule(MasterVestingScheduleDTO dto);
	TenantVestingScheduleDTO getTenantVestingScheduleById(UUID id);
	List<TenantVestingScheduleDTO> getAllTenantVestingSchedules();
	TenantVestingScheduleDTO createTenantVestingSchedule(TenantVestingScheduleDTO dto);
	List<VestingScheduleDetailDTO> getVestingScheduleDetailsByMasterId(UUID masterId);
	List<VestingScheduleDetailDTO> getVestingScheduleDetailsByTenantId(UUID tenantId);
	VestingScheduleDetailDTO createVestingScheduleDetail(VestingScheduleDetailDTO dto);
} 