package team.nine.booknutsbackend.dto.Response;

import lombok.Builder;
import lombok.Getter;
import team.nine.booknutsbackend.domain.Series.Series;
import team.nine.booknutsbackend.domain.Series.SeriesBoard;
import team.nine.booknutsbackend.domain.User;

import java.util.List;

@Getter
@Builder
public class SeriesResponse {

    Long seriesId;
    String title;
    String content;
    String imgUrl;
    int totalPost;
    int totalNuts;

    public static SeriesResponse myStoryResponse(Series series, User user) {
        return SeriesResponse.builder()
                .seriesId(series.getSeriesId())
                .title(series.getTitle())
                .content(series.getContent())
                .imgUrl(series.getImgUrl())
                .totalPost(series.getSeriesBoardList().size())
                .totalNuts(getTotalNuts(series))
                .build();
    }

    private static int getTotalNuts(Series series) {
        List<SeriesBoard> seriesBoardList = series.getSeriesBoardList();
        int totalNuts = 0;
        for (SeriesBoard seriesBoard : seriesBoardList) {
            totalNuts += seriesBoard.getBoard().getNutsCnt();
        }
        return totalNuts;
    }

}
