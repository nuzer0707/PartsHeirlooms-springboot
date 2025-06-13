package com.example.demo.mapper;

import com.example.demo.model.dto.TransactionMethodDto;
import com.example.demo.model.entity.TransactionMethod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionMethodMapper {

    @Autowired
    private ModelMapper modelMapper;

    public TransactionMethodDto toDto(TransactionMethod entity) {
        return modelMapper.map(entity, TransactionMethodDto.class);
    }
}