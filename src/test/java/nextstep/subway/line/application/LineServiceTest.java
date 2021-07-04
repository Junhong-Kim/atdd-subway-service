package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nextstep.subway.ServiceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("노선 서비스 테스트 - 실제 객체 사용")
public class LineServiceTest extends ServiceTest {

	@Autowired
	private LineService lineService;
	@Autowired
	private StationService stationService;

	private LineRequest 일호선_요청;
	private LineRequest 오호선_요청;
	private StationResponse 종로3가역;
	private StationResponse 신길역;

	@BeforeEach
	void 초기화() {
		// given
		종로3가역 = stationService.saveStation(new StationRequest("종로3가역"));
		신길역 = stationService.saveStation(new StationRequest("신길역"));

		일호선_요청 = new LineRequest("1호선", "blue", 종로3가역.getId(), 신길역.getId(), 10);
		오호선_요청 = new LineRequest("5호선", "purple", 종로3가역.getId(), 신길역.getId(), 10);
	}

	@Test
	void 노선_등록() {
		// when
		LineResponse 일호선_응답 = lineService.saveLine(일호선_요청);

		// then
		등록_요청_정보와_응답_정보가_같음(일호선_응답);
	}

	@Test
	void 이미_등록된_노선_등록_요청하는_경우_등록되지_않음() {
		// given
		노선_등록되어_있음(일호선_요청);

		// when

		// then
		등록되지_않음(일호선_요청);
	}

	@Test
	void 노선_목록() {
		// given
		LineResponse 일호선_응답 = 노선_등록되어_있음(일호선_요청);
		LineResponse 오호선_응답 = 노선_등록되어_있음(오호선_요청);

		// when
		List<LineResponse> 노선_목록 = lineService.findLines();

		// then
		지하철_노선_목록_포함됨(노선_목록, Arrays.asList(일호선_응답, 오호선_응답));
	}

	@Test
	void 노선_조회() {
		// given
		LineResponse 일호선_응답 = 노선_등록되어_있음(일호선_요청);

		// when
		LineResponse 조회_노선 = lineService.findLineResponseById(일호선_응답.getId());

		// then
		등록_요청_정보와_응답_정보가_같음(조회_노선);
	}

	private void 등록_요청_정보와_응답_정보가_같음(LineResponse 응답_정보) {
		assertThat(응답_정보.getName()).isEqualTo(일호선_요청.getName());
		assertThat(응답_정보.getColor()).isEqualTo(일호선_요청.getColor());
		assertThat(응답_정보.getStations()).containsSequence(Arrays.asList(종로3가역, 신길역));
	}

	private LineResponse 노선_등록되어_있음(LineRequest 요청) {
		return lineService.saveLine(요청);
	}

	private void 등록되지_않음(LineRequest 일호선_요청) {
		assertThatThrownBy(() -> lineService.saveLine(일호선_요청)).isInstanceOf(RuntimeException.class);
	}

	public static void 지하철_노선_목록_포함됨(List<LineResponse> 노선_목록, List<LineResponse> 비교할_노선_목록) {
		List<Long> expectedLineIds = 노선_목록.stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());

		List<Long> resultLineIds = 비교할_노선_목록.stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());

		assertThat(resultLineIds).containsAll(expectedLineIds);
	}
}
