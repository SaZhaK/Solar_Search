import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {first, map, tap} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [HttpClient]
})
export class AppComponent {
  BASE_URL = 'http://localhost:8080'

  searchRequest = null;
  searchResponse = null;
  snippet = [];
  currentSnippetPath = null;
  selectedRow = -1;

  constructor(private http: HttpClient) {}

  onSubmit(): void {
    this.selectedRow = -1;
    this.snippet = [];
    this.currentSnippetPath = null;

    this.http.get(this.BASE_URL+ '/search/' + this.searchRequest)
      .pipe(
          first(),
          map(result => this.searchResponse = (result as any).lines)
      ).subscribe();
  }

  rowClick(index, linePath, lineNumber): void {
    this.selectedRow = index;
    this.currentSnippetPath = linePath;

    const params = new HttpParams()
          .set('linePath', linePath)
          .set('lineNumber', lineNumber);
    this.http.get(this.BASE_URL + '/search/snippet', {params})
      .pipe(
          first(),
          map(result => this.snippet = (result as any))
      ).subscribe();
  }

  reducePath(path): string {
      if (path.length > 30) {
        const firstIdx = path.indexOf('/');
        const lastIdx = path.lastIndexOf('/');

        const reducedPath = path.substring(0, firstIdx + 1) + '...' + path.substring(lastIdx);
        return reducedPath;
      } else {
        return path;
      }
  }

  replaceEmptyString(line): string {
    if (line.trim() === '') {
      return '\u00A0';
    } else {
      return line;
    }
  }

  printMatches(): string {
    const matchesCount = this.searchResponse.length;
    if (matchesCount === 1) {
      return matchesCount + ' match';
    } else {
      return matchesCount + ' matches';
    }
  }
}
