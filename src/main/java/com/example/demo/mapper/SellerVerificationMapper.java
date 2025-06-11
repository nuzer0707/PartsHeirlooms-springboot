package com.example.demo.mapper;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.SellerVerificationApplyDto;
import com.example.demo.model.dto.SellerVerificationDto;
import com.example.demo.model.entity.SellerVerification;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.VerificationImage;

@Component
public class SellerVerificationMapper {

	@Autowired
	private ModelMapper modelMapper;

	public SellerVerificationDto toDto(SellerVerification verification) {
		if (verification == null) {
			return null;
		}
		SellerVerificationDto dto = modelMapper.map(verification, SellerVerificationDto.class);
		if (verification.getUser() != null) {
			dto.setUserId(verification.getUser().getUserId());
			dto.setUsername(verification.getUser().getUsername());
		}
		if (verification.getReviewedByAdmin() != null) {
			dto.setReviewedByAdminId(verification.getReviewedByAdmin().getUserId());
			dto.setReviewedByAdminUsername(verification.getReviewedByAdmin().getUsername());
		}
		if (verification.getVerificationImages() != null) {
			dto.setVerificationImageBases64(verification.getVerificationImages().stream()
					.map(VerificationImage::getImageBase64).collect(Collectors.toList()));
		}
		return dto;
	}

	public SellerVerification toEntity(SellerVerificationApplyDto applyDto, User applicantUser) {
		if (applyDto == null) {
			return null;
		}

		SellerVerification entity = new SellerVerification();
		entity.setUser(applicantUser);

		if (applyDto.getVerificationImageBases64() != null) {
			entity.setVerificationImages(applyDto.getVerificationImageBases64().stream().map(imageBase64 -> {
				VerificationImage vi = new VerificationImage();
				vi.setImageBase64(imageBase64);
				vi.setSellerVerification(entity);
				return vi;
			}).collect(Collectors.toList()));

		}

		return entity;

	}

}
