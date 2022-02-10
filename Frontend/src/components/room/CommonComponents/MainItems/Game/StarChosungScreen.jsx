import React from 'react';
import styled from 'styled-components';
import { MainDiv } from '../Main.style';
import { useSelector, useDispatch } from 'react-redux';
import UserVideoComponent from '../../../../../pages/Room/DongJun/UserVideoComponent';

const StarScreen = styled.div`
  overflow-x: auto;
  position: relative;
  width: 60.041vw;
  height: 82vh;
  max-height: 82vh;
  background-color: white;
  border-radius: 3.0643vh;
  box-shadow: 0.306vh 0.306vh gray;
`;

const PerScPosition = styled.div`
  position: absolute;
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  top: 2%;
  left: 3%;
  height: 82vh;
`;

export default function StarChosungScreen() {
  const { subscribers, storeSession } = useSelector(state => ({
    subscribers: state.MeetingRoom.subscribers,
    storeSession: state.MeetingRoom.storeSession,
  }));
  return (
    <MainDiv>
      <StarScreen>
        <PerScPosition>
          {subscribers &&
            subscribers.map((sub, i) => (
              <div
                // className="stream-container col-md-6 col-xs-6"
                key={i}
              >
                <UserVideoComponent streamManager={sub} />
              </div>
            ))}
        </PerScPosition>
      </StarScreen>
    </MainDiv>
  );
}
