package ltd.ligma.vorovayka.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.model.payload.ClientMeta;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClientMetaProvider {
    private final UserAgentAnalyzer userAgentAnalyzer;

    private static final String HTTP_HEADER_PROXY_REAL_IP = "X-Real-IP";

    public ClientMeta retrieveClientMeta(HttpServletRequest request) {
        var meta = new ClientMeta();

        var tz = RequestContextUtils.getTimeZone(request);
        if (tz != null) meta.setTimeZone(tz.getID());

        meta.setIp(retrieveRemoteAddress(request));

        var ua = userAgentAnalyzer.parse(Collections.list(request.getHeaderNames()).stream().collect(Collectors.toMap(h -> h, request::getHeader)));
        meta.setClientName(String.join(",", ua.getValue("AgentClass"), ua.getValue("AgentName")));
        meta.setSystem(String.join(",", ua.getValue("OperatingSystemClass"), ua.getValue("OperatingSystemName")));
        meta.setCpu(String.join(",", ua.getValue("DeviceCpu"), ua.getValue("DeviceCpuBits")));

        return meta;
    }

    public static String calculateHash(ClientMeta clientMeta) {
        var ua = String.join("&",
                clientMeta.getIp(),
                clientMeta.getCpu(),
                clientMeta.getSystem(),
                clientMeta.getTimeZone(),
                clientMeta.getClientName());
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ua.getBytes(StandardCharsets.UTF_8));
            return new String(Hex.encode(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // TODO: Handle exception
        }
    }

    private static String retrieveRemoteAddress(HttpServletRequest request) {
        String realIp = request.getHeader(HTTP_HEADER_PROXY_REAL_IP);
        if (StringUtils.hasText(realIp)) return realIp;
        return request.getRemoteAddr();

    }
}
