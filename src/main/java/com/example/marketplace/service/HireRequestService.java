import com.example.marketplace.model.HireRequest;
import com.example.marketplace.repository.HireRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class HireRequestService {
    private final HireRequestRepository hireRequestRepository;
    private final HouseHelpRepository houseHelpRepository;

    public HireRequest createHireRequest(HireRequest dto, User buyer) {
        HouseHelp houseHelp = houseHelpRepository.findById(dto.getHouseHelpId())
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));

        HireRequest request = new HireRequest();
        request.setBuyer(buyer);
        request.setHouseHelp(houseHelp);
        request.setStatus(RequestStatus.PENDING);
        request.setStartDate(dto.getStartDate());
        request.setMessage(dto.getMessage());

        return hireRequestRepository.save(request);
    }
    public void updateStatus(Long requestId, RequestStatus status) {
        HireRequest request = hireRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        hireRequestRepository.save(request);
    }

    public List<HireRequest> getRequestsForHouseHelp(Long houseHelpId) {
        return hireRequestRepository.findByHouseHelpId(houseHelpId);
    }
}