package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.UserTimetableRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUserTimetableAdapterTest {

    @InjectMocks
    private DeleteUserTimetableAdapter adapter;

    @Mock
    private UserTimetableRepository repository;

    @DisplayName("userId로 시간표를 삭제한다.")
    @Test
    void deleteAllByUser() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        //when
        adapter.deleteAllByUser(user);

        //then
        verify(repository).deleteByUserId(1L);
    }
}
