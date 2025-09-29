package com.example.mamolog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

	 // アプリルートに uploads フォルダを作る想定（必要なら外部パスに変更）
    private final Path uploadDir = Paths.get("uploads");

    @PostConstruct
    public void init() throws IOException {
        // 起動時に uploads フォルダを作成（存在すれば無視）
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    // ユーザーがフォームで選んだ画像（MultipartFile)を受け取り、ユニーク名で保存してファイル名を返す
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        // 元ファイル名から拡張子を取る（簡易）
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx > 0) ext = original.substring(idx);

        String filename = UUID.randomUUID().toString() + ext;
        Path target = uploadDir.resolve(filename);		// uploads/filename というパスを作る
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return filename;		// DBにはファイル名だけ保存
    }

    // ファイル削除（レコード削除時に呼ぶ）
    public void delete(String filename) {
        if (filename == null) return;
        try {
            Files.deleteIfExists(uploadDir.resolve(filename));
        } catch (IOException e) {
            // ログだけ出す。削除失敗で処理を止めない。
            e.printStackTrace();
        }
    }
}
