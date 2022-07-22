package com.example.accord;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class GenreSelect extends AppCompatActivity {

    private static final String TAG = "GenreSelect";
    JSONObject metadata;
    Button bNext;
    Button bPop;
    Button bRock;
    Button bCountry;
    Button bEDM;
    Button bLatin;
    Button bMetal;
    Button bRap;
    Button bHipHop;
    Button bPopPunk;
    Button bFolk;
    Button bMuscial;
    Button bPunkRock;
    boolean pop = false;
    boolean rock = false;
    boolean country = false;
    boolean EDM = false;
    boolean latin = false;
    boolean metal = false;
    boolean rap = false;
    boolean hipHop = false;
    boolean popPunk = false;
    boolean folk = false;
    boolean musical = false;
    boolean punkRock = false;
    public int selectCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_select);
        initMetadata();
        setup();
        listeners();
    }

    public void initMetadata() {
        String mString = Parcels.unwrap(getIntent().getParcelableExtra("metadata"));
        try {
            metadata = new JSONObject(mString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, String.valueOf(metadata));
    }

    public void setup() {
        selectCount = 0;
        bNext = findViewById(R.id.bContinue);
        bNext.setVisibility(View.GONE);
        bPop = findViewById(R.id.bPop);
        bPop.setBackgroundColor(Color.DKGRAY);
        bRock = findViewById(R.id.bRock);
        bRock.setBackgroundColor(Color.DKGRAY);
        bCountry = findViewById(R.id.bCountry);
        bCountry.setBackgroundColor(Color.DKGRAY);
        bEDM = findViewById(R.id.bEDM);
        bEDM.setBackgroundColor(Color.DKGRAY);
        bLatin = findViewById(R.id.bLatino);
        bLatin.setBackgroundColor(Color.DKGRAY);
        bMetal = findViewById(R.id.bMetal);
        bMetal.setBackgroundColor(Color.DKGRAY);
        bRap = findViewById(R.id.bRap);
        bRap.setBackgroundColor(Color.DKGRAY);
        bHipHop = findViewById(R.id.bHipHop);
        bHipHop.setBackgroundColor(Color.DKGRAY);
        bPopPunk = findViewById(R.id.bPopPunk);
        bPopPunk.setBackgroundColor(Color.DKGRAY);
        bFolk = findViewById(R.id.bFolk);
        bFolk.setBackgroundColor(Color.DKGRAY);
        bMuscial = findViewById(R.id.bMusical);
        bMuscial.setBackgroundColor(Color.DKGRAY);
        bPunkRock = findViewById(R.id.bPunkRock);
        bPunkRock.setBackgroundColor(Color.DKGRAY);
    }

    public void listeners() {
        bPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pop) {
                    pop = false;
                    bPop.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    pop = true;
                    bPop.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rock) {
                    rock = false;
                    bRock.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    rock = true;
                    bRock.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (country) {
                    country = false;
                    bCountry.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    country = true;
                    bCountry.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bMetal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (metal) {
                    metal = false;
                    bMetal.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    metal = true;
                    bMetal.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bRap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rap) {
                    rap = false;
                    bRap.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    rap = true;
                    bRap.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bHipHop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hipHop) {
                    hipHop = false;
                    bHipHop.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    hipHop = true;
                    bHipHop.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bPopPunk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popPunk) {
                    popPunk = false;
                    bPopPunk.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    popPunk = true;
                    bPopPunk.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bFolk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folk) {
                    folk = false;
                    bFolk.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    folk = true;
                    bFolk.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bEDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EDM) {
                    EDM = false;
                    bEDM.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    EDM = true;
                    bEDM.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bLatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latin) {
                    latin = false;
                    bLatin.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    latin = true;
                    bLatin.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bMuscial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musical) {
                    musical = false;
                    bMuscial.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    musical = true;
                    bMuscial.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bPunkRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (punkRock) {
                    punkRock = false;
                    bPunkRock.setBackgroundColor(Color.DKGRAY);
                    selectCount--;
                } else if (selectCount < 5) {
                    punkRock = true;
                    bPunkRock.setBackgroundColor(Color.RED);
                    selectCount++;
                }
                checkCount();
            }
        });

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void checkCount() {
        if (selectCount == 0) {
            bNext.setVisibility(View.GONE);
        } else {
            bNext.setVisibility(View.VISIBLE);
        }
    }

    public void createData() throws JSONException {
        Log.e(TAG, "Creating metadata");
        JSONArray genreArr = new JSONArray();
        if (rock) {
            genreArr.put("rock");
        }
        if (pop) {
            genreArr.put("pop");
        }
        if (country) {
            genreArr.put("country");
        }
        if (metal) {
            genreArr.put("metal");
        }
        if (rap) {
            genreArr.put("rap");
        }
        if (hipHop) {
            genreArr.put("hiphop");
        }
        if (popPunk) {
            genreArr.put("poppunk");
        }
        if (folk) {
            genreArr.put("folk");
        }
        if (EDM) {
            genreArr.put("edm");
        }
        if (latin) {
            genreArr.put("latin");
        }
        if (musical) {
            genreArr.put("musical");
        }
        if (punkRock) {
            genreArr.put("punkrock");
        }
        metadata.put("Genres", genreArr);
        Log.e(TAG, "Metadata: " + metadata);
        Intent i = new Intent(this, CreateAccountActivity.class);
        i.putExtra("metadata", Parcels.wrap(metadata.toString()));
        startActivity(i);
    }
}