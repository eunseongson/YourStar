package com.ssafy.yourstar.domain.meeting.service;

import com.ssafy.yourstar.domain.meeting.db.entity.Applicant;
import com.ssafy.yourstar.domain.meeting.db.entity.ApplicantID;
import com.ssafy.yourstar.domain.meeting.db.entity.Meeting;
import com.ssafy.yourstar.domain.meeting.db.repository.ApplicantRepository;
import com.ssafy.yourstar.domain.meeting.db.repository.MeetingRepository;
import com.ssafy.yourstar.domain.meeting.db.repository.MeetingRepositorySpp;
import com.ssafy.yourstar.domain.meeting.request.MeetingApplyByStarPostReq;
import com.ssafy.yourstar.domain.meeting.request.MeetingApplyByUserPostReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {
    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    ApplicantRepository applicantRepository;

    @Autowired
    MeetingRepositorySpp meetingRepositorySpp;


    @Override
    public Meeting meetingApplyByStar(MeetingApplyByStarPostReq meetingApplyByStarPostReq) {
        Meeting meeting = new Meeting();

        meeting.setManagerCode(meetingApplyByStarPostReq.getManagerCode());
        meeting.setMeetingName(meetingApplyByStarPostReq.getMeetingName());
        meeting.setMeetingOpenDate(meetingApplyByStarPostReq.getMeetingOpenDate());
        meeting.setMeetingStartDate(meetingApplyByStarPostReq.getMeetingStartDate());
        meeting.setMeetingEndDate(meetingApplyByStarPostReq.getMeetingEndDate());
        meeting.setMeetingCnt(meetingApplyByStarPostReq.getMeetingCnt());
        meeting.setMeetingPrice(meetingApplyByStarPostReq.getMeetingPrice());
        meeting.setMeetingDescription(meetingApplyByStarPostReq.getMeetingDescription());
        meeting.setApprove(false); // 스타가 신청시에는 관리자 승인 X 상태로 저장

        return meetingRepository.save(meeting);
    }

    @Override
    public Meeting meetingModifyByStar(Meeting meeting) {
        // 해당 팬미팅이 존재하면 수정
        if (meetingRepository.findById(meeting.getMeetingId()).isPresent()) {
            return meetingRepository.save(meeting);
        } else return null;
    }

    @Override
    public boolean meetingRemoveByStar(int meetingId) {
        // 해당 팬미팅이 존재하면 삭제
        if (meetingRepository.findById(meetingId).isPresent()) {
            meetingRepository.deleteById(meetingId);
            return true;
        } else return false;
    }

    @Override
    public Page<Meeting> meetingList(Pageable pageable) {
        return meetingRepository.findAll(pageable);
    }

    @Override
    public Meeting meetingDetail(int meetingId) {
        if (meetingRepository.findById(meetingId).isPresent()) {
            return meetingRepository.findById(meetingId).get();
        }
        return null;
    }

    @Override
    public Page<Meeting> meetingPendingList(Pageable pageable) {

        return meetingRepository.findAllByIsApproveFalse(pageable);
    }

    @Override
    public boolean meetingPendingApprove(int meetingId) {
        if (meetingRepository.findById(meetingId).isPresent()) {
            Meeting meeting = meetingRepository.findById(meetingId).get();

            // 승인 상태로 변경 후 저장
            meeting.setApprove(true);
            meetingRepository.save(meeting);
            return true;
        }
        return false;
    }

    @Override
    public Page<Meeting> meetingApproveList(Pageable pageable) {
        return meetingRepository.findAllByIsApproveTrue(pageable);
    }

    @Override
    public Applicant meetingApplyByUser(MeetingApplyByUserPostReq meetingApplyByUserPostReq) {
        Applicant applicant = new Applicant();

        applicant.setMeetingId(meetingApplyByUserPostReq.getMeetingId());
        applicant.setMemberId(meetingApplyByUserPostReq.getMemberId());
        applicant.setApplicantWarnCount(0); // 신청 했을 때 경고 횟수는 0이다.

        return applicantRepository.save(applicant);
    }

    @Override
    public boolean meetingRemoveByUser(int memberId, int meetingId) {
        // 복합키이기 때문에 ID에 내용을 등록 후 사용
        ApplicantID applicantID = new ApplicantID();
        applicantID.setMemberId(memberId);
        applicantID.setMeetingId(meetingId);

        // 해당 팬미팅이 존재하는지 조회 후 있을 때 삭제
        if (applicantRepository.findById(applicantID).isPresent()) {
            applicantRepository.deleteById(applicantID);

            return true;
        }
        return false;
    }

    @Override
    public Page<Meeting> meetingApplyListByUser(int memberId, Pageable pageable) {
        // queryDSL을 사용한 코드
        return meetingRepositorySpp.findAllApplyMeetingByMemberId(memberId, pageable);
//        return applicantRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    public Applicant applicantDetail(int memberId, int meetingId) {
        ApplicantID applicantID = new ApplicantID();
        applicantID.setMemberId(memberId);
        applicantID.setMeetingId(meetingId);

        // 값이 있다면 리턴 아니면 null
        if (applicantRepository.findById(applicantID).isPresent()) {
            return applicantRepository.findById(applicantID).get();
        } else {
            return null;
        }
    }

    @Override
    public boolean meetingGiveWarnToUser(int memberId, int meetingId) {
        ApplicantID applicantID = new ApplicantID();
        applicantID.setMemberId(memberId);
        applicantID.setMeetingId(meetingId);

        // 값이 있다면 경고 업데이트 아니면 false
        if (applicantRepository.findById(applicantID).isPresent()) {
            Applicant applicant = applicantRepository.findById(applicantID).get();
            applicant.setApplicantWarnCount(applicant.getApplicantWarnCount() + 1); // 현재 경고 횟수에서 +1
            applicantRepository.save(applicant); // 값 업데이트

            return true;
        } else {
            return false;
        }
    }
}
